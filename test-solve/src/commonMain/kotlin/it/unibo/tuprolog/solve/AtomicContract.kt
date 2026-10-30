package it.unibo.tuprolog.solve

/** Conformance tests for the ISO `atomic/1` type-checking built-in. */
fun atomicContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("atomic/atomic-atom") { atomic("atom") yields { listOf(it.yes()) } }
    case("atomic/atomic-aof-b") { atomic("a"("b")) yields { listOf(it.no()) } }
    case("atomic/atomic-var") { atomic("Var") yields { listOf(it.no()) } }
    case("atomic/atomic-empty-list") { atomic(emptyLogicList) yields { listOf(it.yes()) } }
    case("atomic/atomic-num") { atomic(6) yields { listOf(it.yes()) } }
    case("atomic/atomic-num-dec") { atomic(3.3) yields { listOf(it.yes()) } }
}
