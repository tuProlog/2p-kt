package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtomCodes] cases. */
fun atomCodesContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtomCodes.prototype(solverFactory) }) {
    case("atom-codes/atom-codes-second-is-var1") { testAtomCodesSecondIsVar1() }
    case("atom-codes/atom-codes-second-is-var") { testAtomCodesSecondIsVar() }
    case("atom-codes/atom-codes-first-is-var") { testAtomCodesFirstIsVar() }
    case("atom-codes/atom-codes-no-var") { testAtomCodesNoVar() }
    case("atom-codes/atom-codes-fail") { testAtomCodesFail() }
}
