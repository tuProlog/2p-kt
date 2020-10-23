@file:JvmName("ComparisonUtils")

package it.unibo.tuprolog.core

import kotlin.jvm.JvmName

expect fun compareStringsLocaleIndependently(string1: String, string2: String): Int
