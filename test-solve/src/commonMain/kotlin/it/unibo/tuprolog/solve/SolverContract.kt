package it.unibo.tuprolog.solve

/** Kotest registration of the [TestSolver] cases. */
fun solverContract(
    solverFactory: SolverFactory,
    callErrorSignature: Signature = Signature("call", 1),
    nafErrorSignature: Signature = Signature("\\+", 1),
    notErrorSignature: Signature = Signature("not", 1),
    disabled: Set<String> = emptySet(),
) = contract(
    disabled,
    { TestSolver.prototype(solverFactory, callErrorSignature, nafErrorSignature, notErrorSignature) },
) {
    case("solver/unknown-flag1") { testUnknownFlag1() }
    case("solver/unknown-flag2") { testUnknownFlag2() }
    case("solver/builtin-api") { testBuiltinApi() }
    case("solver/assert") { testAssert() }
    case("solver/assert-z") { testAssertZ() }
    case("solver/assert-a") { testAssertA() }
    case("solver/write") { testWrite() }
    case("solver/standard-output") { testStandardOutput() }
    case("solver/find-all") { testFindAll() }
    case("solver/side-effects-persistent-after-backtracking1") { testSideEffectsPersistentAfterBacktracking1() }
    case("solver/true") { testTrue() }
    case("solver/timeout1") { testTimeout1() }
    case("solver/timeout2") { testTimeout2() }
    case("solver/timeout3") { testTimeout3() }
    case("solver/timeout4") { testTimeout4() }
    case("solver/if-then1") { testIfThen1() }
    case("solver/if-then-else1") { testIfThenElse1() }
    case("solver/if-then-else2") { testIfThenElse2() }
    case("solver/if-then2") { testIfThen2() }
    case("solver/unification") { testUnification() }
    case("solver/simple-cut-alternatives") { testSimpleCutAlternatives() }
    case("solver/cut-and-conjunction") { testCutAndConjunction() }
    case("solver/cut-conjunction-and-backtracking") { testCutConjunctionAndBacktracking() }
    case("solver/max-duration-parameter-and-time-out-exception") { testMaxDurationParameterAndTimeOutException() }
    case("solver/prolog-standard-search-tree-example") { testPrologStandardSearchTreeExample() }
    case("solver/prolog-standard-search-tree-with-cut-example") { testPrologStandardSearchTreeWithCutExample() }
    case("solver/backtracking-with-custom-reverse-list-implementation") {
        testBacktrackingWithCustomReverseListImplementation()
    }
    case("solver/with-prolog-standard-conjunction-examples") { testWithPrologStandardConjunctionExamples() }
    case("solver/conjunction-properties") { testConjunctionProperties() }
    case("solver/call-primitive") { testCallPrimitive() }
    case("solver/call-primitive-transparency") { testCallPrimitiveTransparency() }
    case("solver/catch-primitive") { testCatchPrimitive() }
    case("solver/catch-primitive-transparency") { testCatchPrimitiveTransparency() }
    case("solver/halt-primitive") { testHaltPrimitive() }
    case("solver/not-primitive") { testNotPrimitive() }
    case("solver/not-modularity") { testNotModularity() }
    case("solver/if-then-rule") { testIfThenRule() }
    case("solver/if-then-else-rule") { testIfThenElseRule() }
    case("solver/numbers-range-list-generation") { testNumbersRangeListGeneration() }
    case("solver/failure") { testFailure() }
    case("solver/basic-backtracking1") { testBasicBacktracking1() }
    case("solver/basic-backtracking2") { testBasicBacktracking2() }
    case("solver/basic-backtracking3") { testBasicBacktracking3() }
    case("solver/basic-backtracking4") { testBasicBacktracking4() }
    case("solver/conjunction") { testConjunction() }
    case("solver/conjunction-of-conjunctions") { testConjunctionOfConjunctions() }
    case("solver/conjunction-with-unification") { testConjunctionWithUnification() }
    case("solver/disjunction") { testDisjunction() }
    case("solver/disjunction-with-unification") { testDisjunctionWithUnification() }
    case("solver/member") { testMember() }
    case("solver/assert-rules") { testAssertRules() }
    case("solver/retract") { testRetract() }
    case("solver/natural") { testNatural() }
    case("solver/functor") { testFunctor() }
    case("solver/univ") { testUniv() }
    case("solver/append") { testAppend() }
    case("solver/retract-all") { testRetractAll() }
    case("solver/term-greater-than") { testTermGreaterThan() }
    case("solver/term-greater-than-or-equal") { testTermGreaterThanOrEqual() }
    case("solver/term-same") { testTermSame() }
    case("solver/term-not-same") { testTermNotSame() }
    case("solver/term-lower-than") { testTermLowerThan() }
    case("solver/term-lower-than-or-equal") { testTermLowerThanOrEqual() }
}
