package it.unibo.tuprolog.solve

/** Kotest registration of the [TestOnce] cases. */
fun onceContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("once", 1),
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestOnce.prototype(solverFactory, errorSignature) }) {
    case("once/once-cut") { testOnceCut() }
    case("once/once-cut-or") { testOnceCutOr() }
    case("once/once-repeat") { testOnceRepeat() }
    case("once/once-fail") { testOnceFail() }
    case("once/once-num") { testOnceNum() }
    case("once/once-x") { testOnceX() }
}
