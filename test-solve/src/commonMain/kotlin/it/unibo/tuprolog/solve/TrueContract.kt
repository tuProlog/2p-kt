package it.unibo.tuprolog.solve

/** Conformance tests for `true/0`. */
fun trueContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("true/true") { atomOf("true") yields { kotlin.collections.listOf(it.yes()) } }
}
