package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCustomData] cases. */
fun customDataContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCustomData.prototype(solverFactory) }) {
    case("custom-data/api") { testApi() }
    case("custom-data/ephemeral-data") { testEphemeralData() }
    case("custom-data/durable-data") { testDurableData() }
    case("custom-data/persistent-data") { testPersistentData() }
}
