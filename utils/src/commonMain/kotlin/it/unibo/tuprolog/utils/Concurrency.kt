@file:JvmName("Concurrency")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

expect fun <K, V> Map<K, V>.toThreadSafe(): Map<K, V>

expect fun <K, V> MutableMap<K, V>.toMutableThreadSafe(): MutableMap<K, V>

expect fun <T> Set<T>.toThreadSafe(): Set<T>

expect fun <T> MutableSet<T>.toMutableThreadSafe(): MutableSet<T>

expect fun <T> List<T>.toThreadSafe(): List<T>

expect fun <T> MutableList<T>.toMutableThreadSafe(): MutableList<T>
