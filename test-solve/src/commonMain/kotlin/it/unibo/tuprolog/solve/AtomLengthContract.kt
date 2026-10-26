package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtomLength] cases. */
fun atomLengthContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtomLength.prototype(solverFactory) }) {
    case("atom-length/atom-length-no-var") { testAtomLengthNoVar() }
    case("atom-length/atom-length-second-is-var") { testAtomLengthSecondIsVar() }
    case("atom-length/atom-length-first-is-var") { testAtomLengthFirstIsVar() }
    case("atom-length/atom-length-second-is-var2") { testAtomLengthSecondIsVar2() }
    case("atom-length/atom-length-fail") { testAtomLengthFail() }
}
