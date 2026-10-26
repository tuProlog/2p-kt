package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtomConcat] cases. */
fun atomConcatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtomConcat.prototype(solverFactory) }) {
    case("atom-concat/atom-concat-third-is-var") { testAtomConcatThirdIsVar() }
    case("atom-concat/atom-concat-fails") { testAtomConcatFails() }
    case("atom-concat/atom-concat-fails-not-prefix") { testAtomConcatFailsNotPrefix() }
    case("atom-concat/atom-concat-fails-not-suffix") { testAtomConcatFailsNotSuffix() }
    case("atom-concat/atom-concat-second-is-var") { testAtomConcatSecondIsVar() }
    case("atom-concat/atom-concat-first-is-var") { testAtomConcatFirstIsVar() }
}
