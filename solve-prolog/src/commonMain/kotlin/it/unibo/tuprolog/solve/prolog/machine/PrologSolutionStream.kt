package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionStream

/**
 * The [SolutionStream] of a `:solve-prolog` query (#1010): [solutions] are the query's, as `solve` post-processes them,
 * and [machine] is the machine computing them, which answers a cancellation with a `Halt` and disposes of its choice
 * points when closed.
 */
internal class PrologSolutionStream(
    val cancellation: Cancellation,
) : SolutionStream {
    lateinit var solutions: Iterator<Solution>
    var machine: PrologMachine? = null
    private var closed = false

    override val isCancelled: Boolean
        get() = cancellation.isCancelled

    override fun hasNext(): Boolean = !closed && solutions.hasNext()

    override fun next(): Solution {
        if (closed) throw NoSuchElementException()
        return solutions.next()
    }

    override fun cancel() = cancellation.cancel()

    override fun close() {
        cancellation.cancel()
        closed = true
        machine?.close()
    }
}
