package it.unibo.tuprolog.solve

/** Kotest registration of the [TestNotProvable] cases. */
fun notProvableContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("not", 1),
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestNotProvable.prototype(solverFactory, errorSignature) }) {
    case("not-provable/np-true") { testNPTrue() }
    case("not-provable/np-cut") { testNPCut() }
    case("not-provable/np-cut-fail") { testNPCutFail() }
    case("not-provable/or-not-cut-fail") { testOrNotCutFail() }
    case("not-provable/np-equals") { testNPEquals() }
    case("not-provable/np-num") { testNPNum() }
    case("not-provable/npx") { testNPX() }
}
