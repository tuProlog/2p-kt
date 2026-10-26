package it.unibo.tuprolog.solve

/** Kotest registration of the [TestArith] cases. */
fun arithContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestArith.prototype(solverFactory) }) {
    case("arith/arith-diff") { testArithDiff() }
    case("arith/arith-eq") { testArithEq() }
    case("arith/arith-greater-than") { testArithGreaterThan() }
    case("arith/arith-greater-than-eq") { testArithGreaterThanEq() }
    case("arith/arith-less-than") { testArithLessThan() }
    case("arith/arith-less-than-eq") { testArithLessThanEq() }
}
