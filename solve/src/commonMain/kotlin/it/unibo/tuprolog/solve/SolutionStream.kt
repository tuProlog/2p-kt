package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * The solutions of one query, as [Solver.solve] returns them (#1010): a single-pass, lazy [Sequence] of [Solution]s
 * that can be cancelled, and closed to release what the resolution holds.
 *
 * Use it in a `use { }` block (Kotlin), a try-with-resources (Java, via [PlatformSolutionStream]'s `stream()` or
 * [asIterable]), or a `for...of` loop (JavaScript, where leaving the loop early closes it):
 * ```kotlin
 * solver.solve(goal).use { solutions ->
 *     for (solution in solutions) { ... }
 * }
 * ```
 * A stream that is abandoned without being closed, e.g. after `take(1)`, is only left to the garbage collector:
 * resources that primitives hold for their pending answers (files, cursors, connections) are never released.
 *
 * - [cancel] asks the resolution to stop. It may be called from any thread, or from the resolution's own callbacks
 *   (an interceptor, a primitive), and more than once. The solution being computed, or the next one asked for, is a
 *   [Solution.Halt] whose exception is a [it.unibo.tuprolog.solve.exception.ResolutionCancelledException]; then the
 *   stream ends. How soon the resolution notices depends on the solver: `:solve-prolog` checks at every inference
 *   step, other solvers may only check between solutions, and some end without the [Solution.Halt]. Cancellation
 *   cannot interrupt a primitive that blocks.
 * - [close] stops the resolution without a further solution, and releases what it holds right away, such as the
 *   alternatives primitives have yet to produce (see [it.unibo.tuprolog.solve.primitive.Primitive]). Call it from the
 *   thread consuming the stream; from other threads, call [cancel].
 *
 * The stream can be iterated only once: a second [iterator] call throws [IllegalStateException]. Operations such as
 * `map` or `take` are lazy, as on any [Sequence], but what they return is a plain [Sequence]: close the stream itself.
 *
 * Solvers implement it by extending [AbstractSolutionStream].
 */
interface SolutionStream :
    Sequence<Solution>,
    AutoCloseable,
    PlatformSolutionStream {
    /** Whether [cancel] or [close] was called. */
    @JsName("isCancelled")
    val isCancelled: Boolean

    /** Asks the resolution to stop: the next solution is a [Solution.Halt], then the stream ends. */
    @JsName("cancel")
    fun cancel()

    /** Stops the resolution without a further solution, releasing what it holds. */
    override fun close()

    /** This stream as a single-pass [Iterable], e.g. for Java's for-each loop. */
    @JsName("asIterable")
    fun asIterable(): Iterable<Solution> = Iterable { iterator() }

    /** Every remaining solution, in order; then this stream is closed. Never returns on an infinite stream. */
    @JsName("toList")
    fun toList(): List<Solution> =
        use {
            val solutions = mutableListOf<Solution>()
            iterator().forEach { solutions += it }
            solutions
        }
}
