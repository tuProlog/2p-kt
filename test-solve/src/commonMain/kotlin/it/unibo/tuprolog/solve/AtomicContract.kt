package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtomic] cases. */
fun atomicContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtomic.prototype(solverFactory) }) {
    case("atomic/atomic-atom") { testAtomicAtom() }
    case("atomic/atomic-aof-b") { testAtomicAofB() }
    case("atomic/atomic-var") { testAtomicVar() }
    case("atomic/atomic-empty-list") { testAtomicEmptyList() }
    case("atomic/atomic-num") { testAtomicNum() }
    case("atomic/atomic-num-dec") { testAtomicNumDec() }
}
