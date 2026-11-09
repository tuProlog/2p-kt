package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.solve.AbstractSolutionStream
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionStream

/** The solutions of [stream], transformed by [transform]; cancelling or closing it cancels or closes [stream]. */
internal class MappedSolutionStream(
    private val stream: SolutionStream,
    private val transform: (Solution) -> Solution,
) : AbstractSolutionStream() {
    private val solutions = stream.iterator()

    override val isCancelled: Boolean
        get() = stream.isCancelled

    override fun hasNextSolution(): Boolean = solutions.hasNext()

    override fun nextSolution(): Solution = transform(solutions.next())

    override fun cancel() = stream.cancel()

    override fun close() = stream.close()
}

internal fun SolutionStream.mapSolutions(transform: (Solution) -> Solution): SolutionStream =
    MappedSolutionStream(this, transform)
