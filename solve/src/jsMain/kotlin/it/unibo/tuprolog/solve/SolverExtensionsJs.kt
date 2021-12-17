package it.unibo.tuprolog.solve

internal actual fun solverFactory(className: String, vararg classNames: String): SolverFactory =
    sequenceOf(className, *classNames)
        .map { JsClassName.parse(it) }
        .map { it.resolve() }
        .filterIsInstance<SolverFactory>()
        .firstOrNull()
        ?: throw IllegalStateException("No viable implementation for ${SolverFactory::class.simpleName}")

actual fun classicSolverFactory(): SolverFactory =
    solverFactory(
        "@tuprolog/2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory",
        "2p-solve-classic:it.unibo.tuprolog.solve.classic.ClassicSolverFactory"
    )

actual fun streamsSolverFactory(): SolverFactory =
    solverFactory(
        "@tuprolog/2p-solve-streams:it.unibo.tuprolog.solve.streams.StreamsSolverFactory",
        "2p-solve-streams:it.unibo.tuprolog.solve.streams.StreamsSolverFactory"
    )

actual fun problogSolverFactory(): SolverFactory =
    solverFactory(
        "@tuprolog/2p-solve-problog:it.unibo.tuprolog.solve.problog.ProblogSolverFactory",
        "2p-solve-problog:it.unibo.tuprolog.solve.problog.ProblogSolverFactory"
    )
