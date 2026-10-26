package it.unibo.tuprolog.solve

/** Kotest registration of the [TestCopyTerm] cases. */
fun copyTermContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestCopyTerm.prototype(solverFactory) }) {
    case("copy-term/copy-x-num") { testCopyXNum() }
    case("copy-term/copy-any-a") { testCopyAnyA() }
    case("copy-term/copy-sum") { testCopySum() }
    case("copy-term/copy-any-any") { testCopyAnyAny() }
    case("copy-term/copy-triple-sum") { testCopyTripleSum() }
    case("copy-term/copy-aa") { testCopyAA() }
    case("copy-term/copy-ab") { testCopyAB() }
    case("copy-term/copy-f") { testCopyF() }
    case("copy-term/double-copy") { testDoubleCopy() }
}
