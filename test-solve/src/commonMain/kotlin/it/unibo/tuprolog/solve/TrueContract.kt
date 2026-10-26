package it.unibo.tuprolog.solve

/** Kotest registration of the [TestTrue] cases. */
fun trueContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestTrue.prototype(solverFactory) }) {
    case("true/true") { testTrue() }
}
