package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAssertA] cases. */
fun assertAContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAssertA.prototype(solverFactory) }) {
    case("assert-a/assert-a-clause") { testAssertAClause() }
    case("assert-a/assert-a-any") { testAssertAAny() }
    case("assert-a/assert-a-number") { testAssertANumber() }
    case("assert-a/assert-a-foo-number") { testAssertAFooNumber() }
    case("assert-a/assert-a-atom-true") { testAssertAAtomTrue() }
}
