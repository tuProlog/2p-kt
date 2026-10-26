package it.unibo.tuprolog.solve

/** Kotest registration of the [TestAtom] cases. */
fun atomContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestAtom.prototype(solverFactory) }) {
    case("atom/atom-atom") { testAtomAtom() }
    case("atom/atom-string") { testAtomString() }
    case("atom/atom-aof-b") { testAtomAofB() }
    case("atom/atom-var") { testAtomVar() }
    case("atom/atom-empty-list") { testAtomEmptyList() }
    case("atom/atom-num") { testAtomNum() }
    case("atom/atom-num-dec") { testAtomNumDec() }
}
