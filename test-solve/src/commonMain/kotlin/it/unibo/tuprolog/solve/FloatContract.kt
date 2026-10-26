package it.unibo.tuprolog.solve

/** Kotest registration of the [TestFloat] cases. */
fun floatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestFloat.prototype(solverFactory) }) {
    case("float/float-dec") { testFloatDec() }
    case("float/float-dec-neg") { testFloatDecNeg() }
    case("float/float-nat") { testFloatNat() }
    case("float/float-atom") { testFloatAtom() }
    case("float/float-x") { testFloatX() }
}
