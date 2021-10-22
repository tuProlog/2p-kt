package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentSolverImpl :
    TestConcurrentSolver<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    override val callErrorSignature: Signature = Signature("ensure_executable", 1)
    override val nafErrorSignature: Signature = Signature("ensure_executable", 1)
    override val notErrorSignature: Signature = Signature("ensure_executable", 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testUnknownFlag2() = multiRunConcurrentTest { super.testUnknownFlag2() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testUnknownFlag1() = multiRunConcurrentTest {
        super.testUnknownFlag1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSideEffectsPersistentAfterBacktracking1() = multiRunConcurrentTest {
        super.testSideEffectsPersistentAfterBacktracking1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFindAll() = multiRunConcurrentTest {
        super.testFindAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAssert() = multiRunConcurrentTest {
        super.testAssert()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAssertZ() = multiRunConcurrentTest {
        super.testAssertZ()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAssertA() = multiRunConcurrentTest {
        super.testAssertA()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testWrite() = multiRunConcurrentTest {
        super.testWrite()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testStandardOutput() = multiRunConcurrentTest {
        super.testStandardOutput()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTrue() = multiRunConcurrentTest {
        super.testTrue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThen1() = multiRunConcurrentTest {
        super.testIfThen1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThen2() = multiRunConcurrentTest {
        super.testIfThen2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenElse1() = multiRunConcurrentTest {
        super.testIfThenElse1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenElse2() = multiRunConcurrentTest {
        super.testIfThenElse2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTimeout1() = multiRunConcurrentTest {
        super.testTimeout1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTimeout2() = multiRunConcurrentTest {
        super.testTimeout2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTimeout3() = multiRunConcurrentTest {
        super.testTimeout3()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTimeout4() = multiRunConcurrentTest {
        super.testTimeout4()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testUnification() = multiRunConcurrentTest {
        super.testUnification()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSimpleCutAlternatives() = multiRunConcurrentTest {
        super.testSimpleCutAlternatives()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCutAndConjunction() = multiRunConcurrentTest {
        super.testCutAndConjunction()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCutConjunctionAndBacktracking() = multiRunConcurrentTest {
        super.testCutConjunctionAndBacktracking()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testMaxDurationParameterAndTimeOutException() = multiRunConcurrentTest {
        super.testMaxDurationParameterAndTimeOutException()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testPrologStandardSearchTreeExample() = multiRunConcurrentTest {
        super.testPrologStandardSearchTreeExample()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testPrologStandardSearchTreeWithCutExample() = multiRunConcurrentTest {
        super.testPrologStandardSearchTreeWithCutExample()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBacktrackingWithCustomReverseListImplementation() = multiRunConcurrentTest {
        super.testBacktrackingWithCustomReverseListImplementation()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testWithPrologStandardConjunctionExamples() = multiRunConcurrentTest {
        super.testWithPrologStandardConjunctionExamples()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testConjunctionProperties() = multiRunConcurrentTest {
        super.testConjunctionProperties()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallPrimitive() = multiRunConcurrentTest {
        super.testCallPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCallPrimitiveTransparency() = multiRunConcurrentTest {
        super.testCallPrimitiveTransparency()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCatchPrimitive() = multiRunConcurrentTest {
        super.testCatchPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testCatchPrimitiveTransparency() = multiRunConcurrentTest {
        super.testCatchPrimitiveTransparency()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testHaltPrimitive() = multiRunConcurrentTest {
        super.testHaltPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNotPrimitive() = multiRunConcurrentTest {
        super.testNotPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNotModularity() = multiRunConcurrentTest {
        super.testNotModularity()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenRule() = multiRunConcurrentTest {
        super.testIfThenRule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testIfThenElseRule() = multiRunConcurrentTest {
        super.testIfThenElseRule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNumbersRangeListGeneration() = multiRunConcurrentTest {
        super.testNumbersRangeListGeneration()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testConjunction() = multiRunConcurrentTest {
        super.testConjunction()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testConjunctionWithUnification() = multiRunConcurrentTest {
        super.testConjunctionWithUnification()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBuiltinApi() = multiRunConcurrentTest {
        super.testBuiltinApi()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDisjunction() = multiRunConcurrentTest {
        super.testDisjunction()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFailure() = multiRunConcurrentTest {
        super.testFailure()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testDisjunctionWithUnification() = multiRunConcurrentTest {
        super.testDisjunctionWithUnification()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testConjunctionOfConjunctions() = multiRunConcurrentTest {
        super.testConjunctionOfConjunctions()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testMember() = multiRunConcurrentTest {
        super.testMember()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBasicBacktracking1() = multiRunConcurrentTest {
        super.testBasicBacktracking1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBasicBacktracking2() = multiRunConcurrentTest {
        super.testBasicBacktracking2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBasicBacktracking3() = multiRunConcurrentTest {
        super.testBasicBacktracking3()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testBasicBacktracking4() = multiRunConcurrentTest {
        super.testBasicBacktracking4()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAssertRules() = multiRunConcurrentTest {
        super.testAssertRules()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testRetract() = multiRunConcurrentTest {
        super.testRetract()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNatural() = multiRunConcurrentTest {
        super.testNatural()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunctor() = multiRunConcurrentTest {
        super.testFunctor()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testUniv() = multiRunConcurrentTest {
        super.testUniv()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testRetractAll() = multiRunConcurrentTest {
        super.testRetractAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAppend() = multiRunConcurrentTest {
        super.testAppend()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermGreaterThan() = multiRunConcurrentTest {
        super.testTermGreaterThan()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermSame() = multiRunConcurrentTest {
        super.testTermSame()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermLowerThan() = multiRunConcurrentTest {
        super.testTermLowerThan()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermGreaterThanOrEqual() = multiRunConcurrentTest {
        super.testTermGreaterThanOrEqual()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermLowerThanOrEqual() = multiRunConcurrentTest {
        super.testTermLowerThanOrEqual()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testTermNotSame() = multiRunConcurrentTest {
        super.testTermNotSame()
    }
}
