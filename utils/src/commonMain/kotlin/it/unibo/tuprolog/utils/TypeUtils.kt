@file:JvmName("TypeUtils")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName
import kotlin.reflect.KClass

internal const val HEX_RADIX = 16

expect fun <T> Any?.forceCast(): T

expect fun box(value: Any): Any

expect val Any.identifier: String

expect val KClass<*>.fullName: String

expect val KClass<*>.name: String

expect val KClass<*>.safeName: String
