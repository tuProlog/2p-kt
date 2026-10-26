package it.unibo.tuprolog.solve

/** Kotest registration of the [TestUnify] cases. */
fun unifyContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestUnify.prototype(solverFactory) }) {
    case("unify/number-with-same-number") { testNumberUnify() }
    case("unify/variable-with-number") { testNumberXUnify() }
    case("unify/variable-with-variable") { testXYUnify() }
    case("unify/conjunction-binding-shared-variable") { testDoubleUnify() }
    case("unify/compounds-binding-both-sides") { testFDefUnify() }
    case("unify/different-numbers-fail") { testDiffNumberUnify() }
    case("unify/integer-with-real-fails") { testDecNumberUnify() }
    case("unify/different-functors-fail") { testGUnifyFX() }
    case("unify/different-arities-fail") { testFUnify() }
    case("unify/different-arities-with-repeated-variables-fail") { testFMultipleTermUnify() }
    case("unify/chained-variable-bindings") { testMultipleTermUnify() }
}
