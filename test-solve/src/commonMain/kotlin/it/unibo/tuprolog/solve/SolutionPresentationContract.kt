package it.unibo.tuprolog.solve

/** Kotest registration of the [TestSolutionPresentation] cases. */
fun solutionPresentationContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestSolutionPresentation.prototype(solverFactory) }) {
    case("solution-presentation/solution-with-dandling-vars") { testSolutionWithDandlingVars() }
}
