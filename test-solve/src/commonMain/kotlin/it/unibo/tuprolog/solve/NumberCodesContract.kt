package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNumberCodes] cases. */
fun numberCodesContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNumberCodes.prototype(solverFactory) }) {
    case("number-codes/number-codes-list-is-var") { testNumberCodesListIsVar() }
    case("number-codes/number-codes-num-is-decimal") { testNumberCodesNumIsDecimal() }
    case("number-codes/number-codes-list-is-var2") { testNumberCodesListIsVar2() }
    case("number-codes/number-codes-ok") { testNumberCodesOk() }
    case("number-codes/number-codes-complete-test") { testNumberCodesCompleteTest() }
    case("number-codes/number-codes-negative-number") { testNumberCodesNegativeNumber() }
    case("number-codes/number-codes-char") { testNumberCodesChar() }
}
