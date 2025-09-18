package it.unibo.tuprolog.solve

@Suppress("SwallowedException")
internal actual fun solverFactory(
    className: String,
    vararg classNames: String,
): SolverFactory =
    sequenceOf(className, *classNames)
        .map {
            try {
                Class.forName(it).kotlin
            } catch (e: ClassNotFoundException) {
                null
            }
        }.filterNotNull()
        .map { it.objectInstance }
        .filterIsInstance<SolverFactory>()
        .firstOrNull()
        ?: error("No viable implementation for ${SolverFactory::class.simpleName}")

actual fun classicSolverFactory(): SolverFactory = solverFactory(FactoryClassNames.CLASSIC)

actual fun streamsSolverFactory(): SolverFactory = solverFactory(FactoryClassNames.STREAMS)

actual fun problogSolverFactory(): SolverFactory = solverFactory(FactoryClassNames.PROBLOG)

actual fun concurrentSolverFactory(): SolverFactory = solverFactory(FactoryClassNames.CONCURRENT)
