package it.unibo.tuprolog.solve

internal object FactoryClassNames {
    const val PROLOG = "it.unibo.tuprolog.solve.prolog.PrologSolverFactory"
    const val PROBLOG = "it.unibo.tuprolog.solve.problog.ProblogSolverFactory"
    const val CONCURRENT = "it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory"
}

/** Platform-specific lookup of a [SolverFactory] implementation class, tried in order from [className] and [classNames]. */
internal expect fun solverFactory(
    className: String,
    vararg classNames: String,
): SolverFactory

/**
 * Platform-specific lookup of the `:solve-prolog` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.prolog]).
 */
expect fun prologSolverFactory(): SolverFactory

/** Platform-specific lookup of the `:solve-concurrent` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.concurrent]). */
expect fun concurrentSolverFactory(): SolverFactory

/** Platform-specific lookup of the `:solve-problog` [SolverFactory] (backing [it.unibo.tuprolog.solve.Solver.problog]). */
expect fun problogSolverFactory(): SolverFactory
