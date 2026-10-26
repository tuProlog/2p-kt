package it.unibo.tuprolog.solve

/** Kotest registration of the [TestSubstitutions] cases. */
fun substitutionsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestSubstitutions.prototype(solverFactory) }) {
    case("substitutions/interesting-variables-are-not-obliterated") { interestingVariablesAreNotObliterated() }
    case("substitutions/interesting-variables-are-properly-tracked") { interestingVariablesAreProperlyTracked() }
    case("substitutions/uninteresting-variables-are-obliterated") { uninterestingVariablesAreObliterated() }
}
