@file:JvmName("ThreadSafety")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

/**
 * Runs [action] while holding the monitor/lock associated with [obj], returning its result; a
 * platform-agnostic, expression-based counterpart of the JVM's `synchronized(obj) { ... }` block, usable
 * from common code. On platforms without real concurrency (e.g. JS), this may simply invoke [action]
 * directly, since no synchronization is needed there.
 */
expect fun <T> synchronizedOn(
    obj: Any,
    action: () -> T,
): T

/**
 * Runs [action] while holding the monitor/lock associated with this receiver object, returning its result.
 * Shorthand for `synchronizedOn(this, action)`, used e.g. by the internal `LRUCache` and `SimpleLRUCache`
 * implementations of [Cache] to guard all their read/write operations:
 * ```kotlin
 * override val size
 *     get() = synchronizedOnSelf { cache.size }
 * ```
 */
fun <T : Any, R> T.synchronizedOnSelf(action: () -> R): R = synchronizedOn(this, action)
