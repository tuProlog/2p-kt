package it.unibo.tuprolog.solve

import kotlin.concurrent.Volatile
import kotlin.js.JsName

/**
 * The solutions of one query, as [Solver.solveStream] returns them: an [Iterator] that can be stopped from outside
 * (#1010).
 *
 * - [cancel] asks the resolution to stop. It may be called from any thread, or from the resolution's own callbacks
 *   (an interceptor, a primitive), and more than once. The solution being computed, or the next one asked for, is a
 *   [Solution.Halt] whose exception is a [it.unibo.tuprolog.solve.exception.ResolutionCancelledException]; then the
 *   stream ends. How soon the resolution notices depends on the solver: `:solve-prolog` checks at every inference
 *   step, other solvers may only check between solutions. Cancellation cannot interrupt a primitive that blocks.
 * - [close] stops the resolution without a further solution, and releases what it holds right away, such as the
 *   alternatives primitives have yet to produce (see [it.unibo.tuprolog.solve.primitive.Primitive]). Call it from the
 *   thread consuming the stream, typically through `use { }`; from other threads, call [cancel].
 *
 * A plain [Solver.solve] sequence that is abandoned, e.g. after `take(1)`, is simply left to the garbage collector:
 * nothing tells the resolution to release resources. Use a [SolutionStream] where that matters.
 */
interface SolutionStream :
    Iterator<Solution>,
    AutoCloseable {
    /** Whether [cancel] or [close] was called. */
    @JsName("isCancelled")
    val isCancelled: Boolean

    /** Asks the resolution to stop: the next solution is a [Solution.Halt], then the stream ends. */
    @JsName("cancel")
    fun cancel()

    /** Stops the resolution without a further solution, releasing what it holds. */
    override fun close()
}

/**
 * A [SolutionStream] over [solutions] that checks cancellation between solutions: once cancelled, the next solution
 * is [onCancel]'s, if any, then the stream ends.
 */
internal class IteratorSolutionStream(
    private val solutions: Iterator<Solution>,
    private val onCancel: () -> Solution? = { null },
) : SolutionStream {
    @Volatile
    private var cancelled = false

    private var closed = false
    private var halted = false

    override val isCancelled: Boolean
        get() = cancelled

    override fun hasNext(): Boolean =
        when {
            closed || halted -> false
            cancelled -> onCancel() != null
            else -> solutions.hasNext()
        }

    override fun next(): Solution {
        if (!closed && !halted && cancelled) {
            halted = true
            return onCancel() ?: throw NoSuchElementException()
        }
        if (!hasNext()) throw NoSuchElementException()
        return solutions.next()
    }

    override fun cancel() {
        cancelled = true
    }

    override fun close() {
        cancelled = true
        closed = true
        (solutions as? AutoCloseable)?.close()
    }
}
