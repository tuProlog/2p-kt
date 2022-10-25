package it.unibo.tuprolog.utils

actual fun <K, V> Map<K, V>.toThreadSafe(): Map<K, V> = this

actual fun <K, V> MutableMap<K, V>.toMutableThreadSafe(): MutableMap<K, V> = this

actual fun <T> Set<T>.toThreadSafe(): Set<T> = this

actual fun <T> MutableSet<T>.toMutableThreadSafe(): MutableSet<T> = this

actual fun <T> List<T>.toThreadSafe(): List<T> = this

actual fun <T> MutableList<T>.toMutableThreadSafe(): MutableList<T> = this
