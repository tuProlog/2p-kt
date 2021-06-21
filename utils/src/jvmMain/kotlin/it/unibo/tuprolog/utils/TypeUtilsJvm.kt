package it.unibo.tuprolog.utils

@Suppress("UNCHECKED_CAST")
actual fun <T> Any.forceCast(): T {
    return this as T
}
