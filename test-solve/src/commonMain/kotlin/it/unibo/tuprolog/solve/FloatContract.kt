package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `float/1` type-checking built-in. */
fun floatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("float/float-dec") { float(3.3) yields { kotlin.collections.listOf(it.yes()) } }
    case("float/float-dec-neg") { float(-3.3) yields { kotlin.collections.listOf(it.yes()) } }
    case("float/float-nat") { float(3) yields { kotlin.collections.listOf(it.no()) } }
    case("float/float-atom") { float("atom") yields { kotlin.collections.listOf(it.no()) } }
    case("float/float-x") { float("X") yields { kotlin.collections.listOf(it.no()) } }
}
