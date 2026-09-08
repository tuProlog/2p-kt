@file:JvmName("CoroutinesUtils")

package it.unibo.tuprolog.solve.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.jvm.JvmName

internal object PoisonPill

internal expect val backgroundScope: CoroutineScope

/**
 * Creates a fresh, platform-specific [CoroutineScope] to run one `:solve-concurrent` resolution in -- one call per
 * [ConcurrentSolver.solveConcurrently] invocation, so that cancelling the scope (e.g. once
 * [it.unibo.tuprolog.solve.SolveOptions.limit] is hit, see [ConcurrentResolutionHandle.terminateResolution]) only
 * affects the coroutines spawned for that particular resolution.
 *
 * On the JVM this is backed by a dedicated cached thread pool, so branches of the search tree can genuinely run on
 * separate OS threads. __On Kotlin/JS this is currently unimplemented__ (`TODO`, throws [NotImplementedError]),
 * meaning `:solve-concurrent` cannot presently be used from JS -- there simply is no JS `actual` scope to launch
 * resolution coroutines onto.
 *
 * @throws NotImplementedError on the current Kotlin/JS target.
 */
expect fun createScope(): CoroutineScope

/**
 * Blocks (on the JVM) until the thread pools backing [createScope]/[backgroundScope] have finished executing
 * every pending coroutine, then shuts them down. Called once at the end of
 * [ConcurrentResolutionHandle.terminateResolution], after a resolution's solution limit has been reached, to make
 * sure no orphaned coroutine keeps running (or holding threads) past that point.
 *
 * __On Kotlin/JS this is currently unimplemented__ (`TODO`, throws [NotImplementedError]).
 *
 * @throws NotImplementedError on the current Kotlin/JS target.
 */
expect fun closeExecution()

/**
 * Converts this [ReceiveChannel] into a (blocking) [Sequence], draining the channel from a coroutine launched on
 * [coroutineScope]. This is what lets [ConcurrentSolver] implement the synchronous [it.unibo.tuprolog.solve.Solver.solve]
 * contract in terms of the channel-based [ConcurrentSolver.solveConcurrently]: elements produced by concurrently
 * running branches of the search tree are funnelled through a queue and replayed, one at a time, to whichever
 * thread iterates the returned [Sequence].
 *
 * On the JVM, iteration blocks the consuming thread until either the next element becomes available or the
 * channel is closed. __On Kotlin/JS this is currently unimplemented__ (`TODO`, throws [NotImplementedError]).
 *
 * @throws NotImplementedError on the current Kotlin/JS target.
 */
expect fun <T> ReceiveChannel<T>.toSequence(coroutineScope: CoroutineScope = backgroundScope): Sequence<T>
