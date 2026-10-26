package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCut] cases. */
fun cutContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCut.prototype(solverFactory) }) {
    case("cut/cut") { testCut() }
    case("cut/cut-fail-true") { testCutFailTrue() }
    case("cut/call-cut-fail-true") { testCallCutFailTrue() }
}
