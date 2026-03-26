// This file was generated automatically. See the README.md file
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.buildtools.api.arguments

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmField
import org.jetbrains.kotlin.buildtools.api.DeprecatedCompilerArgument
import org.jetbrains.kotlin.buildtools.api.KotlinReleaseVersion

/**
 * @since 2.4.0
 */
public interface CommonKlibBasedArguments : CommonCompilerArguments {
  /**
   * Get the value for option specified by [key] if it was previously [set] or if it has a default value.
   *
   * @return the previously set value for an option
   * @throws IllegalStateException if the option was not set and has no default value
   */
  public operator fun <V> `get`(key: CommonKlibBasedArgument<V>): V

  /**
   * Check if an option specified by [key] has a value set.
   *
   * Note: trying to read an option (by using [get]) that has not been set will result in an exception.
   *
   * @return true if the option has a value set, false otherwise
   */
  public operator fun contains(key: CommonKlibBasedArgument<*>): Boolean

  /**
   * An option for configuring [CommonKlibBasedArguments].
   *
   * @see get
   * @see set    
   */
  public class CommonKlibBasedArgument<V>(
    public val id: String,
    public val availableSinceVersion: KotlinReleaseVersion,
  )

  /**
   * A builder for [CommonKlibBasedArguments].
   */
  public interface Builder : CommonCompilerArguments.Builder {
    /**
     * Get the value for option specified by [key] if it was previously [set] or if it has a default value.
     *
     * @return the previously set value for an option
     * @throws IllegalStateException if the option was not set and has no default value
     */
    public operator fun <V> `get`(key: CommonKlibBasedArgument<V>): V

    /**
     * Set the [value] for option specified by [key], overriding any previous value for that option.
     */
    public operator fun <V> `set`(key: CommonKlibBasedArgument<V>, `value`: V)

    /**
     * Check if an option specified by [key] has a value set.
     *
     * Note: trying to read an option (by using [get]) that has not been set will result in an exception.
     *
     * @return true if the option has a value set, false otherwise
     */
    public operator fun contains(key: CommonKlibBasedArgument<*>): Boolean
  }

  public companion object {
    /**
     * This option is deprecated and will be deleted in future versions.
     * The partial linkage engine is always turned on.
     * If you would like to adjust the compile-time log level for partial linkage, use -Xpartial-linkage-loglevel.
     *
     * WARNING: this option is EXPERIMENTAL and it may be changed in the future without notice or may be removed entirely.
     *
     * Deprecated in Kotlin version 2.4.0.
     */
    @JvmField
    @ExperimentalCompilerArgument
    @DeprecatedCompilerArgument
    public val X_PARTIAL_LINKAGE: CommonKlibBasedArgument<String?> =
        CommonKlibBasedArgument("X_PARTIAL_LINKAGE", KotlinReleaseVersion(2, 0, 20))

    /**
     * Define the compile-time log level for partial linkage.
     *
     * WARNING: this option is EXPERIMENTAL and it may be changed in the future without notice or may be removed entirely.
     */
    @JvmField
    @ExperimentalCompilerArgument
    public val X_PARTIAL_LINKAGE_LOGLEVEL: CommonKlibBasedArgument<String?> =
        CommonKlibBasedArgument("X_PARTIAL_LINKAGE_LOGLEVEL", KotlinReleaseVersion(2, 0, 20))

    /**
     * Klib dependencies usage strategy when multiple KLIBs has same `unique_name` property value.
     *
     * WARNING: this option is EXPERIMENTAL and it may be changed in the future without notice or may be removed entirely.
     */
    @JvmField
    @ExperimentalCompilerArgument
    public val X_KLIB_DUPLICATED_UNIQUE_NAME_STRATEGY: CommonKlibBasedArgument<String?> =
        CommonKlibBasedArgument("X_KLIB_DUPLICATED_UNIQUE_NAME_STRATEGY", KotlinReleaseVersion(2, 1, 0))

    /**
     * Maximum number of klibs that can be cached during compilation. Default is 64.
     *
     * WARNING: this option is EXPERIMENTAL and it may be changed in the future without notice or may be removed entirely.
     */
    @JvmField
    @ExperimentalCompilerArgument
    public val X_KLIB_ZIP_FILE_ACCESSOR_CACHE_LIMIT: CommonKlibBasedArgument<Int> =
        CommonKlibBasedArgument("X_KLIB_ZIP_FILE_ACCESSOR_CACHE_LIMIT", KotlinReleaseVersion(2, 3, 0))

    /**
     * Skip library compatibility checks for stdlib and kotlin.test library.
     *
     * WARNING: this option is EXPERIMENTAL and it may be changed in the future without notice or may be removed entirely.
     */
    @JvmField
    @ExperimentalCompilerArgument
    public val X_SKIP_LIBRARY_SPECIAL_COMPATIBILITY_CHECKS: CommonKlibBasedArgument<Boolean> =
        CommonKlibBasedArgument("X_SKIP_LIBRARY_SPECIAL_COMPATIBILITY_CHECKS", KotlinReleaseVersion(2, 4, 0))
  }
}
