/*
 * Copyright 2000-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics







import org.jetbrains.kotlin.psi.psiUtil.closestPsiElement as p1kOriginal_closestPsiElement

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiInvalidElementAccessException
import org.jetbrains.kotlin.psi.KtExpression



open class PsiDiagnosticUtils {
class LineAndColumn {
val line: Int

val column: Int

val lineContent: String?

constructor(line: Int, column: Int, lineContent: String?) {

this.line = line
this.column = column
this.lineContent = lineContent
}

open fun getLine(): Int {

return line
}

open fun getColumn(): Int {

return column
}

open fun getLineContent(): String? {

return lineContent
}

override fun toString(): String {

if (line < 0) {
return "(offset: " + column + " line unknown)"
}
return "(" + line + "," + column + ")"
}

companion object {
val NONE: LineAndColumn = LineAndColumn(- 1,- 1,null)
}
}

class LineAndColumnRange {
val start: LineAndColumn

val end: LineAndColumn

constructor(start: LineAndColumn, end: LineAndColumn) {

this.start = start
this.end = end
}

open fun getStart(): LineAndColumn {

return start
}

open fun getEnd(): LineAndColumn {

return end
}

override fun toString(): String {

if (start.line == end.line) {
return "(" + start.line + "," + start.column + "-" + end.column + ")"
}
return start.toString() + " - " + end
}

companion object {
val NONE: LineAndColumnRange = LineAndColumnRange(LineAndColumn.NONE,LineAndColumn.NONE)
}
}

companion object {
fun atLocation(element: PsiElement): String {

if (element.isValid) {
return atLocation(element.containingFile!!,element.textRange!!)
}
var file: PsiFile? = null
var offset: Int = - 1
try {
file = element.containingFile
offset = element.textOffset
}
catch (invalidException: PsiInvalidElementAccessException) {

}
return "at offset: " +((if(offset != - 1) offset else "<unknown>")) + " file: " +((if(file != null) file else "<unknown>"))
}

fun atLocation(expression: KtExpression): String {

return atLocation(expression.node!!)
}

fun atLocation(node: ASTNode): String {

var startOffset: Int = node.startOffset
var element: PsiElement? = (node).p1kOriginal_closestPsiElement()
if (element != null) {
return atLocation(element)
}
return "at offset " + startOffset + " (line and file unknown: no PSI element)"
}

fun atLocation(file: PsiFile, textRange: TextRange): String {

var document: Document? = file.viewProvider.document
return atLocation(file,textRange,document)
}

fun atLocation(file: PsiFile?, textRange: TextRange, document: Document?): String {

var offset: Int = textRange.startOffset
var virtualFile: VirtualFile? = file!!.virtualFile
var pathSuffix: String? = " in " +((if(virtualFile == null) file.name else virtualFile.path))
return offsetToLineAndColumn(document,offset).toString() + pathSuffix
}

fun offsetToLineAndColumn(document: Document?, offset: Int): LineAndColumn {

if (document == null || document.textLength == 0) {
return LineAndColumn(- 1,offset,null)
}
var lineNumber: Int = document.getLineNumber(offset)
var lineStartOffset: Int = document.getLineStartOffset(lineNumber)
var column: Int = offset - lineStartOffset
var lineEndOffset: Int = document.getLineEndOffset(lineNumber)
var lineContent: CharSequence = document.charsSequence.subSequence(lineStartOffset,lineEndOffset)
return LineAndColumn(lineNumber + 1,column + 1,lineContent.toString())
}
}
}
