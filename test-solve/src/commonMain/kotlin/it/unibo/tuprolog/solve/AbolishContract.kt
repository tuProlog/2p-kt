package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAbolish] cases. */
fun abolishContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAbolish.prototype(solverFactory) }) {
    case("abolish/double-abolish") { testDoubleAbolish() }
    case("abolish/abolish-foo") { testAbolishFoo() }
    case("abolish/abolish-foo-neg") { testAbolishFooNeg() }
    case("abolish/abolish-flag") { testAbolishFlag() }
    case("abolish/abolish") { testAbolish() }
}
