package it.unibo.tuprolog.solve

/** Kotest registration of the [TestFindAll] cases. */
fun findAllContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("call", 1),
    disabled: Set<String> = emptySet(),
) = contract(disabled, { TestFindAll.prototype(solverFactory, errorSignature) }) {
    case("find-all/find-x-in-diff-values") { testFindXInDiffValues() }
    case("find-all/find-sum-result") { testFindSumResult() }
    case("find-all/find-xin-fail") { testFindXinFail() }
    case("find-all/find-xin-same-x-values") { testFindXinSameXValues() }
    case("find-all/result-list-is-correct") { testResultListIsCorrect() }
    case("find-all/find-xto-double-assigment") { testFindXtoDoubleAssigment() }
    case("find-all/find-xin-goal") { testFindXinGoal() }
    case("find-all/find-xin-number") { testFindXinNumber() }
    case("find-all/find-xin-call") { testFindXinCall() }
}
