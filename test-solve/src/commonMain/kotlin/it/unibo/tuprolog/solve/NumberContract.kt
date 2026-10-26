package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `number/1` type-checking built-in. */
fun numberContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("number/basic-num") { number(3) yields { kotlin.collections.listOf(it.yes()) } }
    case("number/dec-num") { number(3.3) yields { kotlin.collections.listOf(it.yes()) } }
    case("number/neg-num") { number(-3) yields { kotlin.collections.listOf(it.yes()) } }
    case("number/letter-num") { number("a") yields { kotlin.collections.listOf(it.no()) } }
    case("number/x-num") { number("X") yields { kotlin.collections.listOf(it.no()) } }
}
