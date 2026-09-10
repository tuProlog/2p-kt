@file:JvmName("FormatterExtensions")

package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName

/** Infix-style shorthand for `formatter.format(this)`, e.g. `myTerm.format(TermFormatter.default())`. */
@JsName("format")
fun <T> T.format(formatter: Formatter<T>): String = formatter.format(this)
