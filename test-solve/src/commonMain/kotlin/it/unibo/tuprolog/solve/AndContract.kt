package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAnd] cases. */
fun andContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAnd.prototype(solverFactory) }) {
    case("and/term-is-free-variable") { testTermIsFreeVariable() }
    case("and/with-substitution") { testWithSubstitution() }
    case("and/fail-is-callable") { testFailIsCallable() }
    case("and/no-foo-is-callable") { testNoFooIsCallable() }
    case("and/true-var-callable") { testTrueVarCallable() }
}
