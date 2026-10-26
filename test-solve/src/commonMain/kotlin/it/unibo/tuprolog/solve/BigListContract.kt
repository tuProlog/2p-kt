package it.unibo.tuprolog.solve

/** Kotest registration of the [TestBigList] cases. */
fun bigListContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestBigList.prototype(solverFactory) }) {
    case("big-list/big-list-generation") { testBigListGeneration() }
}
