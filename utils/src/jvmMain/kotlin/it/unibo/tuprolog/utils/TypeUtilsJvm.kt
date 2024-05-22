package it.unibo.tuprolog.utils

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
actual fun <T> Any?.forceCast(): T {
    return this as T
}

actual fun box(value: Any): Any = value

actual val Any.identifier: String
    get() = System.identityHashCode(this).toString(HEX_RADIX)

actual val KClass<*>.fullName: String
    get() = qualifiedName ?: java.name

actual val KClass<*>.name: String
    get() = simpleName ?: java.simpleName

actual val KClass<*>.safeName: String
    get() = fullName
