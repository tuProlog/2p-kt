package it.unibo.tuprolog.solve

/** Kotest registration of the [TestSetOf] cases. */
fun setOfContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestSetOf.prototype(solverFactory) }) {
    case("set-of/set-of-basic") { testSetOfBasic() }
    case("set-of/set-of-x") { testSetOfX() }
    case("set-of/set-of-sorted") { testSetOfSorted() }
    case("set-of/set-of-doubled") { testSetOfDoubled() }
    case("set-of/set-of-fail") { testSetOfFail() }
    case("set-of/set-of-as-find-all") { testSetOfAsFindAll() }
}
