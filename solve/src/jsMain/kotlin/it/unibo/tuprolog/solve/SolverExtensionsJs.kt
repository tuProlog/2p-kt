package it.unibo.tuprolog.solve

private object ModuleNames {
    const val tuprolog = "@tuprolog"

    private const val solvePrefix = "2p-solve"

    const val classic = "$solvePrefix-classic"

    private fun withOptionalPrefix(orgPrefix: Boolean = false, module: String, klass: String): String =
        (if (orgPrefix) "$tuprolog/" else "") + module + ":" + klass

    fun classicFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, classic, FactoryClassNames.classic)

    const val streams = "$solvePrefix-streams"

    fun streamsFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, streams, FactoryClassNames.streams)

    const val problog = "$solvePrefix-problog"

    fun problogFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, problog, FactoryClassNames.problog)

    const val concurrent = "$solvePrefix-concurrent"

    fun concurrentFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, concurrent, FactoryClassNames.concurrent)
}

internal actual fun solverFactory(className: String, vararg classNames: String): SolverFactory =
    sequenceOf(className, *classNames)
        .map { JsClassName.parse(it) }
        .map { it.resolve() }
        .filterIsInstance<SolverFactory>()
        .firstOrNull()
        ?: throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")

actual fun classicSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.classicFactoryClass(orgPrefix = true),
        ModuleNames.classicFactoryClass(orgPrefix = false)
    )

actual fun streamsSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.streamsFactoryClass(orgPrefix = true),
        ModuleNames.streamsFactoryClass(orgPrefix = false)
    )

actual fun problogSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.problogFactoryClass(orgPrefix = true),
        ModuleNames.problogFactoryClass(orgPrefix = false)
    )

actual fun concurrentSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.concurrentFactoryClass(orgPrefix = true),
        ModuleNames.concurrentFactoryClass(orgPrefix = false)
    )
