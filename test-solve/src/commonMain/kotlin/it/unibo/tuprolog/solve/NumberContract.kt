package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNumber] cases. */
fun numberContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNumber.prototype(solverFactory) }) {
    case("number/basic-num") { testBasicNum() }
    case("number/dec-num") { testDecNum() }
    case("number/neg-num") { testNegNum() }
    case("number/letter-num") { testLetterNum() }
    case("number/x-num") { testXNum() }
}
