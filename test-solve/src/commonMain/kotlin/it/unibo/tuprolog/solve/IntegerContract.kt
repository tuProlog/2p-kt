package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `integer/1` type-checking built-in. */
fun integerContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("integer/int-positive-num") { integer(3) yields { listOf(it.yes()) } }
    case("integer/int-negative-num") { integer(intOf(-3)) yields { listOf(it.yes()) } }
    case("integer/int-dec-num") { integer(3.3) yields { listOf(it.no()) } }
    case("integer/int-x") { integer("X") yields { listOf(it.no()) } }
    case("integer/int-atom") { integer("atom") yields { listOf(it.no()) } }
}
