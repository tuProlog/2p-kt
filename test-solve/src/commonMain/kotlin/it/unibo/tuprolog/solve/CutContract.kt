package it.unibo.tuprolog.solve

/** Conformance tests for `!`/0 (cut). */
fun cutContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("cut/cut") { atomOf("!") yields { kotlin.collections.listOf(it.yes()) } }
    case("cut/cut-fail-true") { ("!" and fail or true) yields { kotlin.collections.listOf(it.no()) } }
    case("cut/call-cut-fail-true") { (call("!") and fail or true) yields { kotlin.collections.listOf(it.yes()) } }
}
