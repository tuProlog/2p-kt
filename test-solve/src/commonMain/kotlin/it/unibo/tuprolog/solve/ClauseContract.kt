package it.unibo.tuprolog.solve

/** Kotest registration of the [TestClause] cases. */
fun clauseContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestClause.prototype(solverFactory) }) {
    case("clause/clause-x-body") { testClauseXBody() }
    case("clause/clause-any-b") { testClauseAnyB() }
    case("clause/clause-num-b") { testClauseNumB() }
    case("clause/clause-f-any-num") { testClauseFAnyNum() }
    case("clause/clause-atom-body") { testClauseAtomBody() }
    case("clause/clause-variables") { testClauseVariables() }
}
