package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNonVar] cases. */
fun nonVarContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNonVar.prototype(solverFactory) }) {
    case("non-var/non-var-number") { testNonVarNumber() }
    case("non-var/non-var-foo") { testNonVarFoo() }
    case("non-var/non-var-foo-cl") { testNonVarFooCl() }
    case("non-var/non-var-foo-assignment") { testNonVarFooAssignment() }
    case("non-var/non-var-any-term") { testNonVarAnyTerm() }
    case("non-var/non-var") { testNonVar() }
}
