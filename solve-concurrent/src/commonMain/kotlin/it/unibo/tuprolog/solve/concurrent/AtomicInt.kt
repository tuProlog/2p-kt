package it.unibo.tuprolog.solve.concurrent

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A tiny, multiplatform abstraction over a mutable integer counter that multiple coroutines may update
 * concurrently. [ConcurrentResolutionHandle] uses it as [ConcurrentResolutionHandle.solutionCounter], to count how
 * many solutions have been produced so far by the many coroutines exploring a goal's search tree in parallel, so
 * that resolution can be stopped once [it.unibo.tuprolog.solve.SolveOptions.limit] is reached.
 *
 * On the JVM this is backed by `java.util.concurrent.atomic.AtomicInteger`, so [value]/[plusAssign]/[minusAssign]/
 * [incAndGet]/[decAndGet] are genuinely atomic and safe to call from any thread. On Kotlin/JS, where coroutines are
 * cooperatively scheduled on a single thread, the implementation is a plain `var`: correct because JS never
 * preempts between the individual (non-suspending) operations below, but not a general-purpose thread-safe
 * counter outside that single-threaded execution model.
 *
 * Instances are created via [zero]/[of] rather than a public constructor, since the concrete implementation is
 * platform-specific (see the internal, `expect`ed `atomicInt` factory function).
 *
 * ```kotlin
 * val counter = AtomicInt.zero()
 * counter += 1
 * counter.incAndGet() // 2
 * ```
 */
interface AtomicInt {
    /** The current value of this counter. Reading/writing this property is atomic on every platform. */
    @JsName("value")
    var value: Int

    /** Atomically adds [delta] to [value]. */
    @JsName("plusAssign")
    operator fun plusAssign(delta: Int)

    /** Atomically subtracts [delta] from [value]. */
    @JsName("minusAssign")
    operator fun minusAssign(delta: Int)

    /** Atomically adds [delta] to [value] and returns the updated value. */
    @JsName("incAndGet")
    fun incAndGet(delta: Int = 1): Int

    /** Atomically subtracts [delta] from [value] and returns the updated value. */
    @JsName("decAndGet")
    fun decAndGet(delta: Int = 1): Int

    companion object {
        /** Creates a new [AtomicInt] initialized to `0`. */
        @JsName("zero")
        @JvmStatic
        fun zero(): AtomicInt = atomicInt(0)

        /** Creates a new [AtomicInt] initialized to [value]. */
        @JsName("of")
        @JvmStatic
        fun of(value: Int): AtomicInt = atomicInt(value)
    }
}

internal expect fun atomicInt(value: Int): AtomicInt
