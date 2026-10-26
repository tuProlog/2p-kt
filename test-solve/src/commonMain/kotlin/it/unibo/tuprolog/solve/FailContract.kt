package it.unibo.tuprolog.solve

/** Kotest registration of the [TestFail] cases. */
fun failContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestFail.prototype(solverFactory) }) {
    case("fail/fail") { testFail() }
    case("fail/undef-pred") { testUndefPred() }
    case("fail/set-flag-fail") { testSetFlagFail() }
    case("fail/set-flag-warning") { testSetFlagWarning() }
}
