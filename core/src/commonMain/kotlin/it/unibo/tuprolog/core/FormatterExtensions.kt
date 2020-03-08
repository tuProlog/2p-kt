@file:JvmName("FormatterExtensions")

package it.unibo.tuprolog.core

import kotlin.jvm.JvmName

fun <T> T.format(formatter: Formatter<T>): String {
    return formatter.format(this)
}

