package it.unibo.tuprolog.solve

private object ModuleNames {
    const val ORGANIZATION = "."

    private const val SOLVE_PREFIX = "2p-solve"

    const val PROLOG = "$SOLVE_PREFIX-prolog"

    private fun withOptionalPrefix(
        orgPrefix: Boolean = false,
        module: String,
        klass: String,
    ): String = (if (orgPrefix) "$ORGANIZATION/" else "") + module + ":" + klass

    fun prologFactoryClass(orgPrefix: Boolean = false) = withOptionalPrefix(orgPrefix, PROLOG, FactoryClassNames.PROLOG)

    const val PROBLOG = "$SOLVE_PREFIX-problog"

    fun problogFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, PROBLOG, FactoryClassNames.PROBLOG)

    const val CONCURRENT = "$SOLVE_PREFIX-concurrent"

    fun concurrentFactoryClass(orgPrefix: Boolean = false) =
        withOptionalPrefix(orgPrefix, CONCURRENT, FactoryClassNames.CONCURRENT)
}

internal actual fun solverFactory(
    className: String,
    vararg classNames: String,
): SolverFactory =
    sequenceOf(className, *classNames)
        .map { JsClassName.parse(it) }
        .map { it.resolve() }
        .filterIsInstance<SolverFactory>()
        .firstOrNull()
        ?: throw IllegalStateException(
            "No viable implementation for ${SolverFactory::class.simpleName} in " +
                sequenceOf(className, *classNames).joinToString(", ", "[", "]"),
        )

actual fun prologSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.prologFactoryClass(orgPrefix = true),
        ModuleNames.prologFactoryClass(orgPrefix = false),
    )

actual fun problogSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.problogFactoryClass(orgPrefix = true),
        ModuleNames.problogFactoryClass(orgPrefix = false),
    )

actual fun concurrentSolverFactory(): SolverFactory =
    solverFactory(
        ModuleNames.concurrentFactoryClass(orgPrefix = true),
        ModuleNames.concurrentFactoryClass(orgPrefix = false),
    )
