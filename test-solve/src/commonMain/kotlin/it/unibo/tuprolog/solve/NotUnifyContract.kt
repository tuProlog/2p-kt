package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNotUnify] cases. */
fun notUnifyContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNotUnify.prototype(solverFactory) }) {
    case("not-unify/number-not-unify") { testNumberNotUnify() }
    case("not-unify/number-x-not-unify") { testNumberXNotUnify() }
    case("not-unify/xy-not-unify") { testXYNotUnify() }
    case("not-unify/double-not-unify") { testDoubleNotUnify() }
    case("not-unify/f-def-not-unify") { testFDefNotUnify() }
    case("not-unify/diff-number-not-unify") { testDiffNumberNotUnify() }
    case("not-unify/dec-number-not-unify") { testDecNumberNotUnify() }
    case("not-unify/g-not-unify-fx") { testGNotUnifyFX() }
    case("not-unify/f-not-unify") { testFNotUnify() }
    case("not-unify/f-multiple-term-not-unify") { testFMultipleTermNotUnify() }
}
