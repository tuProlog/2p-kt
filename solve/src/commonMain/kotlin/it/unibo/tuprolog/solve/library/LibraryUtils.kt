@file:JvmName("LibraryUtils")

package it.unibo.tuprolog.solve.library

import kotlin.js.JsName
import kotlin.jvm.JvmName

/** Converts the current library into a [Runtime] containing a single library */
@JsName("libraryToRuntime")
fun Library.toRuntime(): Runtime = Runtime.of(this)

/**
 * Combines this [Library] and [other] into a two-library [Runtime], via [Runtime.of]. Note that, unlike
 * [Runtime.plus], this does *not* reject a clashing [Library.alias]: if both share the same alias, [other] silently
 * wins.
 */
@JsName("libraryPlusLibrary")
operator fun Library.plus(other: Library): Runtime = Runtime.of(this, other)
