package it.unibo.tuprolog.solve

/** Kotest registration of the [TestStackTrace] cases. */
fun stackTraceContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestStackTrace.prototype(solverFactory) }) {
    case("stack-trace/simple-stack-trace") { testSimpleStackTrace() }
    case("stack-trace/double-stack-trace") { testDoubleStackTrace() }
    case("stack-trace/triple-stack-trace") { testTripleStackTrace() }
    case("stack-trace/throw-is-not-in-stacktrace") { testThrowIsNotInStacktrace() }
}
