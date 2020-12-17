package it.unibo.tuprolog.solve

internal actual fun solverFactory(className: String, vararg classNames: String): SolverFactory {
    throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")
}

actual fun streamsSolverFactory(): SolverFactory {
    throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")
}

actual fun classicSolverFactory(): SolverFactory {
    throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")
}