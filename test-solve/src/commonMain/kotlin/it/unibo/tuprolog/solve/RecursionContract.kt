package it.unibo.tuprolog.solve

/** Kotest registration of the [TestRecursion] cases. */
fun recursionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestRecursion.prototype(solverFactory) }) {
    case("recursion/recursion1") { testRecursion1() }
    case("recursion/recursion2") { testRecursion2() }
    case("recursion/tail-recursion") { testTailRecursion() }
    case("recursion/non-tail-recursion") { testNonTailRecursion() }
}
