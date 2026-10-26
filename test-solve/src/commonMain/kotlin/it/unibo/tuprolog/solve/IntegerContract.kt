package it.unibo.tuprolog.solve

/** Kotest registration of the [TestInteger] cases. */
fun integerContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestInteger.prototype(solverFactory) }) {
    case("integer/int-positive-num") { testIntPositiveNum() }
    case("integer/int-negative-num") { testIntNegativeNum() }
    case("integer/int-dec-num") { testIntDecNum() }
    case("integer/int-x") { testIntX() }
    case("integer/int-atom") { testIntAtom() }
}
