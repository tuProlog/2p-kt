package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAssertZ] cases. */
fun assertZContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAssertZ.prototype(solverFactory) }) {
    case("assert-z/assert-z-clause") { testAssertZClause() }
    case("assert-z/assert-z-any") { testAssertZAny() }
    case("assert-z/assert-z-number") { testAssertZNumber() }
    case("assert-z/assert-z-foo-number") { testAssertZFooNumber() }
    case("assert-z/assert-z-atom-true") { testAssertZAtomTrue() }
}
