package it.unibo.tuprolog.utils

import java.util.Collections

actual fun <K, V> Map<K, V>.toThreadSafe(): Map<K, V> = Collections.synchronizedMap(this)

actual fun <K, V> MutableMap<K, V>.toMutableThreadSafe(): MutableMap<K, V> = Collections.synchronizedMap(this)

actual fun <T> Set<T>.toThreadSafe(): Set<T> = Collections.synchronizedSet(this)

actual fun <T> MutableSet<T>.toMutableThreadSafe(): MutableSet<T> = Collections.synchronizedSet(this)

actual fun <T> List<T>.toThreadSafe(): List<T> = Collections.synchronizedList(this)

actual fun <T> MutableList<T>.toMutableThreadSafe(): MutableList<T> = Collections.synchronizedList(this)
