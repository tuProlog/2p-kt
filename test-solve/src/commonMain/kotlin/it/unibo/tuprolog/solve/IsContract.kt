package it.unibo.tuprolog.solve

/** Kotest registration of the [TestIs] cases. */
fun isContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestIs.prototype(solverFactory) }) {
    case("is/is-result") { testIsResult() }
    case("is/is-xy") { testIsXY() }
    case("is/is-foo") { testIsFoo() }
    case("is/is-n-number") { testIsNNumber() }
    case("is/is-number-foo") { testIsNumberFoo() }
    case("is/is-x-float") { testIsXFloat() }
}
