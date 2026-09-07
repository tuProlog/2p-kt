package it.unibo.tuprolog.solve

internal object FactoryClassNames {
    const val CLASSIC = "it.unibo.tuprolog.solve.classic.ClassicSolverFactory"
    const val STREAMS = "it.unibo.tuprolog.solve.streams.StreamsSolverFactory"
    const val PROBLOG = "it.unibo.tuprolog.solve.problog.ProblogSolverFactory"
    const val CONCURRENT = "it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory"
}

/** Platform-specific lookup of a [SolverFactory] implementation class, tried in order from [className] and [classNames]. */
internal expect fun solverFactory(
    className: String,
    vararg classNames: String,
): SolverFactory

/**
 * Platform-specific lookup of the `:solve-classic` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.prolog]/
 * [it.unibo.tuprolog.solve.Solver.classic]).
 */
expect fun classicSolverFactory(): SolverFactory

/** Platform-specific lookup of the `:solve-concurrent` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.concurrent]). */
expect fun concurrentSolverFactory(): SolverFactory

/** Platform-specific lookup of the `:solve-streams` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.streams]). */
expect fun streamsSolverFactory(): SolverFactory

/** Platform-specific lookup of the `:solve-problog` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.problog]). */
expect fun problogSolverFactory(): SolverFactory
