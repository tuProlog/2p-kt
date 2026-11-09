package it.unibo.tuprolog.solve

import kotlin.concurrent.Volatile

/**
 * A [SolutionStream] over [solutions] that checks cancellation between solutions: once cancelled, the next solution
 * is [onCancel]'s, if any, then the stream ends.
 */
internal class IteratorSolutionStream(
    private val solutions: Iterator<Solution>,
    private val onCancel: () -> Solution? = { null },
) : AbstractSolutionStream() {
    @Volatile
    private var cancelled = false

    private var closed = false
    private var halted = false

    override val isCancelled: Boolean
        get() = cancelled

    override fun hasNextSolution(): Boolean =
        when {
            closed || halted -> false
            cancelled -> onCancel() != null
            else -> solutions.hasNext()
        }

    override fun nextSolution(): Solution {
        if (!closed && !halted && cancelled) {
            halted = true
            return onCancel() ?: throw NoSuchElementException()
        }
        if (!hasNextSolution()) throw NoSuchElementException()
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
