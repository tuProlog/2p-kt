package it.unibo.tuprolog.utils

@Suppress("UNCHECKED_CAST")
actual fun <T> Any?.forceCast(): T = this as T
