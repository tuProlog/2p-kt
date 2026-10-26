package it.unibo.tuprolog.solve

/** Kotest registration of the [TestBagOf] cases. */
fun bagOfContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestBagOf.prototype(solverFactory) }) {
    case("bag-of/bag-x-in-different-values") { testBagXInDifferentValues() }
    case("bag-of/bag-of-find-x") { testBagOfFindX() }
    case("bag-of/bag-of-yxz") { testBagOfYXZ() }
    case("bag-of/bag-of-fail") { testBagOfFail() }
    case("bag-of/bag-of-same-as-findall") { testBagOfSameAsFindall() }
    case("bag-of/bag-of-instance-error") { testBagOfInstanceError() }
    case("bag-of/bag-of-type-error") { testBagOfTypeError() }
}
