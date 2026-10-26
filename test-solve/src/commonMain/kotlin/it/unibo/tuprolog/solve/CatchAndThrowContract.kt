package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCatchAndThrow] cases. */
fun catchAndThrowContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCatchAndThrow.prototype(solverFactory) }) {
    case("catch-and-throw/catch-throw") { testCatchThrow() }
    case("catch-and-throw/catch-fail") { testCatchFail() }
}
