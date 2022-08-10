@file:JvmName("LibraryUtils")

package it.unibo.tuprolog.solve.library

import kotlin.js.JsName
import kotlin.jvm.JvmName

/** Converts the current library into a [Runtime] containing a single library */
@JsName("libraryToRuntime")
fun Library.toRuntime(): Runtime = Runtime.of(this)

@JsName("libraryPlusLibrary")
operator fun Library.plus(other: Library): Runtime = Runtime.of(this, other)
