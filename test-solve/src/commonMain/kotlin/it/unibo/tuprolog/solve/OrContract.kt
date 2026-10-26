package it.unibo.tuprolog.solve

/** Kotest registration of the [TestOr] cases. */
fun orContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestOr.prototype(solverFactory) }) {
    case("or/true-or-false") { testTrueOrFalse() }
    case("or/cut-false-or-true") { testCutFalseOrTrue() }
    case("or/cut-call") { testCutCall() }
    case("or/cut-assigned-value") { testCutAssignedValue() }
    case("or/or-double-assignment") { testOrDoubleAssignment() }
}
