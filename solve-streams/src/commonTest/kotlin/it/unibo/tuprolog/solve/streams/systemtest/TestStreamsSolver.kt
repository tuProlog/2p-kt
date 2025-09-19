package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolver
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsSolver :
    TestSolver,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestSolver.prototype(this)

    override val callErrorSignature: Signature
        get() = prototype.callErrorSignature

    override val nafErrorSignature: Signature
        get() = prototype.nafErrorSignature

    override val notErrorSignature: Signature
        get() = prototype.notErrorSignature

    @Test
    @Ignore
    override fun testUnknownFlag2() {
        prototype.testUnknownFlag2()
    }

    @Test
    @Ignore
    override fun testUnknownFlag1() {
        prototype.testUnknownFlag1()
    }

    @Test
    @Ignore
    override fun testFindAll() {
        prototype.testFindAll()
    }

    @Test
    @Ignore
    override fun testAssert() {
        prototype.testAssert()
    }

    @Test
    @Ignore
    override fun testAssertZ() {
        prototype.testAssertZ()
    }

    @Test
    @Ignore
    override fun testAssertA() {
        prototype.testAssertA()
    }

    @Test
    override fun testWrite() {
        prototype.testWrite()
    }

    @Test
    @Ignore
    override fun testSideEffectsPersistentAfterBacktracking1() {
        prototype.testSideEffectsPersistentAfterBacktracking1()
    }

    @Test
    override fun testStandardOutput() {
        prototype.testStandardOutput()
    }

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }

    @Test
    @Ignore
    override fun testIfThen1() {
        prototype.testIfThen1()
    }

    @Test
    @Ignore
    override fun testIfThen2() {
        prototype.testIfThen2()
    }

    @Test
    @Ignore
    override fun testIfThenElse1() {
        prototype.testIfThenElse1()
    }

    @Test
    @Ignore
    override fun testIfThenElse2() {
        prototype.testIfThenElse2()
    }

    @Test
    override fun testTimeout1() {
        prototype.testTimeout1()
    }

    @Test
    override fun testTimeout2() {
        prototype.testTimeout2()
    }

    @Test
    override fun testTimeout3() {
        prototype.testTimeout3()
    }

    @Test
    override fun testTimeout4() {
        prototype.testTimeout4()
    }

    @Test
    override fun testUnification() {
        prototype.testUnification()
    }

    @Test
    override fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives()
    }

    @Test
    override fun testCutAndConjunction() {
        prototype.testCutAndConjunction()
    }

    @Test
    override fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking()
    }

    @Test
    @Ignore // this does not fail but slows down the build a lot
    override fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException()
    }

    @Test
    override fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample()
    }

    @Test
    @Ignore
    override fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample()
    }

    @Test
    override fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation()
    }

    @Test
    override fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples()
    }

    @Test
    @Ignore
    override fun testConjunctionProperties() {
        prototype.testConjunctionProperties()
    }

    @Test
    @Ignore
    override fun testCallPrimitive() {
        prototype.testCallPrimitive()
    }

    @Test
    @Ignore
    override fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency()
    }

    @Test
    @Ignore
    override fun testCatchPrimitive() {
        prototype.testCatchPrimitive()
    }

    @Test
    @Ignore
    override fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency()
    }

    @Test
    override fun testHaltPrimitive() {
        prototype.testHaltPrimitive()
    }

    @Test
    @Ignore
    override fun testNotPrimitive() {
        prototype.testNotPrimitive()
    }

    @Test
    @Ignore
    override fun testNotModularity() {
        prototype.testNotModularity()
    }

    @Test
    override fun testIfThenRule() {
        prototype.testIfThenRule()
    }

    @Test
    override fun testIfThenElseRule() {
        prototype.testIfThenElseRule()
    }

    @Test
    @Ignore
    override fun testNumbersRangeListGeneration() {
        prototype.testNumbersRangeListGeneration()
    }

    @Test
    override fun testConjunction() {
        prototype.testConjunction()
    }

    @Test
    override fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification()
    }

    @Test
    override fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    override fun testDisjunction() {
        prototype.testDisjunction()
    }

    @Test
    override fun testFailure() {
        prototype.testFailure()
    }

    @Test
    override fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification()
    }

    @Test
    override fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions()
    }

    @Test
    override fun testMember() {
        prototype.testMember()
    }

    @Test
    override fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1()
    }

    @Test
    override fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2()
    }

    @Test
    override fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3()
    }

    @Test
    override fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4()
    }

    @Test
    override fun testAssertRules() {
        prototype.testAssertRules()
    }

    @Test
    override fun testRetract() {
        prototype.testRetract()
    }

    @Test
    override fun testNatural() {
        prototype.testNatural()
    }

    @Test
    @Ignore
    override fun testUniv() {
        prototype.testUniv()
    }

    @Test
    @Ignore
    override fun testFunctor() {
        prototype.testFunctor()
    }

    @Test
    override fun testRetractAll() {
        prototype.testRetractAll()
    }

    @Test
    override fun testAppend() {
        prototype.testAppend()
    }

    @Test
    override fun testTermGreaterThan() {
        prototype.testTermGreaterThan()
    }

    @Test
    override fun testTermSame() {
        prototype.testTermSame()
    }

    @Test
    override fun testTermLowerThan() {
        prototype.testTermLowerThan()
    }

    @Test
    override fun testTermGreaterThanOrEqual() {
        prototype.testTermGreaterThanOrEqual()
    }

    @Test
    override fun testTermLowerThanOrEqual() {
        prototype.testTermLowerThanOrEqual()
    }

    @Test
    override fun testTermNotSame() {
        prototype.testTermNotSame()
    }
}
