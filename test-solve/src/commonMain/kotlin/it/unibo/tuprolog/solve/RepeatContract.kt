package it.unibo.tuprolog.solve

/** Kotest registration of the [TestRepeat] cases. */
fun repeatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestRepeat.prototype(solverFactory) }) {
    case("repeat/repeat") { testRepeat() }
}
