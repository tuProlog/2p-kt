package it.unibo.tuprolog.solve

import java.lang.IllegalStateException

internal actual fun solverFactory(className: String, vararg classNames: String): SolverFactory {
    return sequenceOf(className, *classNames)
        .map {
            try {
                Class.forName(it).kotlin
            } catch (e: ClassNotFoundException) {
                null
            }
        }
        .filterNotNull()
        .map { it.objectInstance }
        .filterIsInstance<SolverFactory>()
        .firstOrNull()
        ?: throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")
}

actual fun classicSolverFactory(): SolverFactory =
    solverFactory(FactoryClassNames.classic)

actual fun streamsSolverFactory(): SolverFactory =
    solverFactory(FactoryClassNames.streams)

actual fun problogSolverFactory(): SolverFactory =
    solverFactory(FactoryClassNames.problog)

actual fun concurrentSolverFactory(): SolverFactory =
    solverFactory(FactoryClassNames.concurrent)
