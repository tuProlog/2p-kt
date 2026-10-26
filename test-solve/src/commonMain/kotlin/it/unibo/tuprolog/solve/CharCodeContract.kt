package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCharCode] cases. */
fun charCodeContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCharCode.prototype(solverFactory) }) {
    case("char-code/char-code-second-is-var") { testCharCodeSecondIsVar() }
    case("char-code/char-code-first-is-var") { testCharCodeFirstIsVar() }
    case("char-code/char-code-type-error") { testCharCodeTypeError() }
    case("char-code/char-code-fails") { testCharCodeFails() }
}
