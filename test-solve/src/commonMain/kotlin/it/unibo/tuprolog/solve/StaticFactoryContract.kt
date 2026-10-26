package it.unibo.tuprolog.solve

/** Kotest registration of the [TestStaticFactory] cases. */
fun staticFactoryContract(
    expectations: Expectations,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestStaticFactory.prototype(expectations) }) {
    case("static-factory/static-solver-factory-for-prolog") { testStaticSolverFactoryForProlog() }
    case("static-factory/static-solver-factory-for-problog") { testStaticSolverFactoryForProblog() }
}
