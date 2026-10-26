package it.unibo.tuprolog.solve

/** Kotest registration of the [TestTerm] cases. */
fun termContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestTerm.prototype(solverFactory) }) {
    case("term/term-diff") { testTermDiff() }
    case("term/term-eq") { testTermEq() }
    case("term/term-greater-than") { testTermGreaterThan() }
    case("term/term-greater-than-eq") { testTermGreaterThanEq() }
    case("term/term-less-than") { testTermLessThan() }
    case("term/term-less-than-eq") { testTermLessThanEq() }
}
