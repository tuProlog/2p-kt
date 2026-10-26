package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCompound] cases. */
fun compoundContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCompound.prototype(solverFactory) }) {
    case("compound/compound-dec") { testCompoundDec() }
    case("compound/compound-neg-dec") { testCompoundNegDec() }
    case("compound/compound-neg-a") { testCompoundNegA() }
    case("compound/compound-any") { testCompoundAny() }
    case("compound/compound-a") { testCompoundA() }
    case("compound/compound-a-of-b") { testCompoundAOfB() }
    case("compound/compound-list-a") { testCompoundListA() }
}
