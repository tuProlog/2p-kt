package it.unibo.tuprolog.solve

/** Kotest registration of the [TestTimeout] cases. */
fun timeoutContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestTimeout.prototype(solverFactory) }) {
    case("timeout/sleep") { testSleep() }
    case("timeout/infinite-find-all") { testInfiniteFindAll() }
    case("timeout/infinite-bag-of") { testInfiniteBagOf() }
    case("timeout/infinite-set-of") { testInfiniteSetOf() }
}
