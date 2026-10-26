package it.unibo.tuprolog.solve

/** Kotest registration of the [TestRetract] cases. */
fun retractContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestRetract.prototype(solverFactory) }) {
    case("retract/retract-num-if-x") { testRetractNumIfX() }
    case("retract/retract-atom-empty-list") { testRetractAtomEmptyList() }
}
