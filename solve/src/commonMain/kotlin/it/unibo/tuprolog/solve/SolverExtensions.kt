package it.unibo.tuprolog.solve

internal expect fun solverFactory(className: String, vararg classNames: String): SolverFactory

expect fun classicSolverFactory(): SolverFactory

expect fun streamsSolverFactory(): SolverFactory
