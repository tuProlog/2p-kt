package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `compound/1` type-checking built-in. */
fun compoundContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("compound/compound-dec") { compound(33.3) yields { listOf(it.no()) } }
    case("compound/compound-neg-dec") { compound(-33.3) yields { listOf(it.no()) } }
    case("compound/compound-neg-a") { compound("-"("a")) yields { listOf(it.yes()) } }
    case("compound/compound-any") { compound(`_`) yields { listOf(it.no()) } }
    case("compound/compound-a") { compound("a") yields { listOf(it.no()) } }
    case("compound/compound-a-of-b") { compound("a"("b")) yields { listOf(it.yes()) } }
    case("compound/compound-list-a") { compound(logicListOf("a")) yields { listOf(it.yes()) } }
}
