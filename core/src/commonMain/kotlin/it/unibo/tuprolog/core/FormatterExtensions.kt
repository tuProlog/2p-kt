@file:JvmName("FormatterExtensions")

package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("format")
fun <T> T.format(formatter: Formatter<T>): String = formatter.format(this)
