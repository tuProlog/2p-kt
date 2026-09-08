@file:JvmName("TypeUtils")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

/**
 * Force-casts this (possibly `null`) receiver to [T], with no runtime type check performed by this function
 * itself (on the JVM, this is a plain `as T` unchecked cast; on JS, it goes through a `dynamic` value,
 * bypassing type checking altogether). Use this only where the caller has independent, external knowledge
 * that the cast is safe (e.g. the [Cursor] `plus` operator in `CursorExtensions.kt` uses it to narrow an
 * [Iterable]-backed `Cursor<out T>` back down to a `Cursor<T>`), since, unlike a checked `as T`, an invalid
 * cast may not fail immediately at the call site, but only later, wherever the miscast value is first used
 * as a [T].
 */
expect fun <T> Any?.forceCast(): T
