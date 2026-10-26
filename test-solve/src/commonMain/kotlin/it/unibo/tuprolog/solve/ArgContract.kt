package it.unibo.tuprolog.solve

/** Kotest registration of the [TestArg] cases. */
fun argContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestArg.prototype(solverFactory) }) {
    case("arg/arg-from-foo") { testArgFromFoo() }
    case("arg/arg-from-foo-x") { testArgFromFooX() }
    case("arg/arg-from-foo2") { testArgFromFoo2() }
    case("arg/arg-from-foo-in-f") { testArgFromFooInF() }
    case("arg/arg-from-foo-y") { testArgFromFooY() }
    case("arg/arg-from-foo-in-second-term") { testArgFromFooInSecondTerm() }
    case("arg/arg-from-foo-in-foo") { testArgFromFooInFoo() }
    case("arg/arg-number-from-foo") { testArgNumberFromFoo() }
    case("arg/arg-x-from-foo") { testArgXFromFoo() }
    case("arg/arg-number-from-x") { testArgNumberFromX() }
    case("arg/arg-from-atom") { testArgFromAtom() }
    case("arg/arg-from-number") { testArgFromNumber() }
    case("arg/negative-arg-from-foo") { testNegativeArgFromFoo() }
    case("arg/arg-a-from-foo") { testArgAFromFoo() }
}
