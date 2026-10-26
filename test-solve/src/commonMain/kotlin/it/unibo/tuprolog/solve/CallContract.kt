package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCall] cases. */
fun callContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("call", 1),
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCall.prototype(solverFactory, errorSignature) }) {
    case("call/call-cut") { testCallCut() }
    case("call/call-fail") { testCallFail() }
    case("call/call-fail-x") { testCallFailX() }
    case("call/call-fail-call") { testCallFailCall() }
    case("call/call-write-x") { testCallWriteX() }
    case("call/call-write-call") { testCallWriteCall() }
    case("call/call-x") { testCallX() }
    case("call/call-one") { testCallOne() }
    case("call/call-fail-one") { testCallFailOne() }
    case("call/call-write-one") { testCallWriteOne() }
    case("call/call-true") { testCallTrue() }
}
