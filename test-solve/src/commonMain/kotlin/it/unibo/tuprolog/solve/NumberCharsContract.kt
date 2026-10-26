package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNumberChars] cases. */
fun numberCharsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNumberChars.prototype(solverFactory) }) {
    case("number-chars/number-chars-list-is-var") { testNumberCharsListIsVar() }
    case("number-chars/number-chars-ok") { testNumberCharsOK() }
    case("number-chars/number-chars-num-is-var") { testNumberCharsNumIsVar() }
    case("number-chars/number-chars-num-negative-is-var") { testNumberCharsNumNegativeIsVar() }
    case("number-chars/number-chars-space") { testNumberCharsSpace() }
    case("number-chars/number-chars-decimal-number") { testNumberCharsDecimalNumber() }
    case("number-chars/number-chars-complete-case") { testNumberCharsCompleteCase() }
    case("number-chars/number-chars-instation-errror") { testNumberCharsInstationErrror() }
}
