package it.unibo.tuprolog.solve

/** Kotest registration of the [TestIfThenElse] cases. */
fun ifThenElseContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestIfThenElse.prototype(solverFactory) }) {
    case("if-then-else/if-true-else-fail") { testIfTrueElseFail() }
    case("if-then-else/if-fail-else-true") { testIfFailElseTrue() }
    case("if-then-else/if-true-then-else-fail") { testIfTrueThenElseFail() }
    case("if-then-else/if-fail-else-fail") { testIfFailElseFail() }
    case("if-then-else/if-x-true-else-x") { testIfXTrueElseX() }
    case("if-then-else/if-fail-else-x") { testIfFailElseX() }
    case("if-then-else/if-then-else-or-with-double-sub") { testIfThenElseOrWithDoubleSub() }
    case("if-then-else/if-or-else-true") { testIfOrElseTrue() }
}
