package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtomChars] cases. */
fun atomCharsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtomChars.prototype(solverFactory) }) {
    case("atom-chars/atom-chars-test-first-is-var") { atomCharsTestFirstIsVar() }
    case("atom-chars/atom-chars-test-yes") { atomCharsTestYes() }
    case("atom-chars/atom-chars-test-one-char-is-var") { atomCharsTestOneCharIsVar() }
    case("atom-chars/atom-chars-test-failure") { atomCharsTestFailure() }
    case("atom-chars/atom-chars-test-empty") { atomCharsTestEmpty() }
    case("atom-chars/atom-chars-test-list-head") { atomCharsTestListHead() }
    case("atom-chars/atom-chars-test-istantation-error-check") { atomCharsTestIstantationErrorCheck() }
    case("atom-chars/atom-chars-test-type-error-check") { atomCharsTestTypeErrorCheck() }
}
