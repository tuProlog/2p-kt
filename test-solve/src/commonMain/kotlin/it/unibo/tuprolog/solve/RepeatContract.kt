package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `repeat/0` built-in. */
fun repeatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("repeat/repeat") { (repeat and (cut and fail)) yields { kotlin.collections.listOf(it.no()) } }
}
