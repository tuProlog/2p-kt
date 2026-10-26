package it.unibo.tuprolog.solve

/** Kotest registration of the [TestDirectives] cases. */
fun directivesContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestDirectives.prototype(solverFactory) }) {
    case("directives/dynamic1") { testDynamic1() }
    case("directives/static1") { testStatic1() }
    case("directives/initialization1") { testInitialization1() }
    case("directives/solve1") { testSolve1() }
    case("directives/wrong-directives") { testWrongDirectives() }
    case("directives/set-flag2") { testSetFlag2() }
    case("directives/set-prolog-flag2") { testSetPrologFlag2() }
    case("directives/op3") { testOp3() }
    case("directives/failing-initialization1") { testFailingInitialization1() }
    case("directives/failing-solve1") { testFailingSolve1() }
    case("directives/exceptional-initialization1") { testExceptionalInitialization1() }
    case("directives/exceptional-solve1") { testExceptionalSolve1() }
    case("directives/directive-loading-quickly") { testDirectiveLoadingQuickly() }
}
