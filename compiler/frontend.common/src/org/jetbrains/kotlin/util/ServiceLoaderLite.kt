/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.util

import com.intellij.platform.native.NativeCharset
import nativekotlin.host.files.NativeFile
import nativekotlin.host.text.jdkJavaIdentifierPart
import nativekotlin.host.text.jdkJavaIdentifierStart
import nativekotlin.host.text.jdkLowercase
import nativekotlin.klib.openKlib
import kotlin.reflect.KClass

/**
 * Original discovery/validation algorithm. Real Native file/ZIP reads
 * and explicit linked class metadata replace only the JVM host operations.
 */
object ServiceLoaderLite {
    private const val SERVICE_DIRECTORY_LOCATION = "META-INF/services/"

    class ServiceLoadingException(val file: NativeFile, cause: Throwable) :
        RuntimeException("Error loading services from $file", cause)

    fun <Service : Any> loadImplementations(service: KClass<Service>, classLoader: NativeServiceClassLoader): List<Service> =
        loadImplementations(service, classLoader.files, classLoader)

    fun <Service : Any> loadImplementations(
        service: KClass<Service>, files: List<NativeFile>, classLoader: NativeServiceClassLoader,
    ): MutableList<Service> {
        val implementations = mutableListOf<Service>()
        for (className in findImplementations(service, files)) {
            val instance = classLoader.newInstance(className)
            if (!service.isInstance(instance)) throw ClassCastException("Cannot cast $className to ${NativeServiceClassNames.binaryName(service)}")
            @Suppress("UNCHECKED_CAST")
            implementations += instance as Service
        }
        return implementations
    }

    inline fun <reified Service : Any> findImplementations(files: List<NativeFile>): Set<String> =
        findImplementations(Service::class, files)
    inline fun <reified Service : Any> loadImplementations(classLoader: NativeServiceClassLoader): List<Service> =
        loadImplementations(Service::class, classLoader)
    inline fun <reified Service : Any> loadImplementations(
        files: List<NativeFile>, classLoader: NativeServiceClassLoader,
    ): List<Service> = loadImplementations(Service::class, files, classLoader)

    fun findImplementations(service: KClass<*>, files: List<NativeFile>): Set<String> =
        files.flatMapTo(linkedSetOf()) { findImplementations(service, it) }

    private fun findImplementations(service: KClass<*>, file: NativeFile): Set<String> {
        val classIdentifier = NativeServiceClassNames.binaryName(service)
        return when {
            file.isDirectory -> findImplementationsInDirectory(classIdentifier, file)
            file.isFile && jdkLowercase(file.extension) == "jar" -> findImplementationsInJar(classIdentifier, file)
            else -> emptySet()
        }
    }

    private fun findImplementationsInDirectory(classId: String, file: NativeFile): Set<String> {
        val serviceFile = NativeFile(file, SERVICE_DIRECTORY_LOCATION + classId).takeIf { it.isFile } ?: return emptySet()
        val contents = try {
            NativeCharset.UTF_8.decode(serviceFile.readBytes())
        } catch (cause: Exception) {
            throw ServiceLoadingException(file, cause)
        }
        return parseLines(file, contents.lineSequence())
    }

    private fun findImplementationsInJar(classId: String, file: NativeFile): Set<String> {
        openKlib(file.path).use { zipFile ->
            val entry = SERVICE_DIRECTORY_LOCATION + classId
            if (entry !in zipFile.paths()) return emptySet()
            val contents = NativeCharset.UTF_8.decode(zipFile.read(entry))
            return parseLines(file, contents.lineSequence())
        }
    }

    private fun parseLines(file: NativeFile, lines: Sequence<String>): Set<String> =
        lines.mapNotNullTo(linkedSetOf()) { parseLine(file, it) }

    private fun parseLine(file: NativeFile, line: String): String? {
        val actualLine = line.substringBefore('#').trim().takeIf { it.isNotEmpty() } ?: return null
        actualLine.forEachIndexed { index, c ->
            val isValid = if (index == 0) jdkJavaIdentifierStart(c.code) else jdkJavaIdentifierPart(c.code) || c == '.'
            if (!isValid) {
                throw ServiceLoadingException(file, RuntimeException("Invalid Java identifier: $line"))
            }
        }
        return actualLine
    }
}
