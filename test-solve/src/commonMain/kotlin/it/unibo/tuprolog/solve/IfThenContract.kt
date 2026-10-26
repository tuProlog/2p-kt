package it.unibo.tuprolog.solve

/** Kotest registration of the [TestIfThen] cases. */
fun ifThenContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestIfThen.prototype(solverFactory) }) {
    case("if-then/if-then-true") { testIfThenTrue() }
    case("if-then/if-then-fail") { testIfThenFail() }
    case("if-then/if-then-fail-true") { testIfThenFailTrue() }
    case("if-then/if-then-xto-one") { testIfThenXtoOne() }
    case("if-then/if-then-x-or") { testIfThenXOr() }
    case("if-then/if-then-or-with-double-sub") { testIfThenOrWithDoubleSub() }
}
