package it.unibo.tuprolog.solve

internal object FactoryClassNames {
    const val CLASSIC = "it.unibo.tuprolog.solve.classic.ClassicSolverFactory"
    const val STREAMS = "it.unibo.tuprolog.solve.streams.StreamsSolverFactory"
    const val PROBLOG = "it.unibo.tuprolog.solve.problog.ProblogSolverFactory"
    const val CONCURRENT = "it.unibo.tuprolog.solve.concurrent.ConcurrentSolverFactory"
}

internal expect fun solverFactory(
    className: String,
    vararg classNames: String,
): SolverFactory

expect fun classicSolverFactory(): SolverFactory

expect fun concurrentSolverFactory(): SolverFactory

expect fun streamsSolverFactory(): SolverFactory

expect fun problogSolverFactory(): SolverFactory
