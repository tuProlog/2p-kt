package it.unibo.tuprolog.solve

expect internal fun solverFactory(className: String, vararg classNames: String): SolverFactory

expect fun classicSolverFactory(): SolverFactory

expect fun streamsSolverFactory(): SolverFactory