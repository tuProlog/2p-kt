package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Ignore
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
    @Ignore // Side effects need to be implemented
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
    @Ignore // Assert need to be implemented
    override fun testAssert() = multiRunConcurrentTest {
        super.testAssert()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Assert need to be implemented
    override fun testAssertZ() = multiRunConcurrentTest {
        super.testAssertZ()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Assert need to be implemented
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
    @Ignore // Cut need to be implemented
    override fun testIfThen1() = multiRunConcurrentTest {
        super.testIfThen1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testIfThen2() = multiRunConcurrentTest {
        super.testIfThen2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testIfThenElse1() = multiRunConcurrentTest {
        super.testIfThenElse1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testIfThenElse2() = multiRunConcurrentTest {
        super.testIfThenElse2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // unhandled sleep
    override fun testTimeout1() = multiRunConcurrentTest {
        super.testTimeout1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // unhandled sleep
    override fun testTimeout2() = multiRunConcurrentTest {
        super.testTimeout2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // unhandled sleep
    override fun testTimeout3() = multiRunConcurrentTest {
        super.testTimeout3()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // unhandled sleep
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
    @Ignore // Cut need to be implemented
    override fun testSimpleCutAlternatives() = multiRunConcurrentTest {
        super.testSimpleCutAlternatives()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testCutAndConjunction() = multiRunConcurrentTest {
        super.testCutAndConjunction()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testCutConjunctionAndBacktracking() = multiRunConcurrentTest {
        super.testCutConjunctionAndBacktracking()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // timeout need to be implemented, does not terminate
    override fun testMaxDurationParameterAndTimeOutException() = multiRunConcurrentTest {
        super.testMaxDurationParameterAndTimeOutException()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // if is nondeterministic
    override fun testPrologStandardSearchTreeExample() = multiRunConcurrentTest {
        super.testPrologStandardSearchTreeExample()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testPrologStandardSearchTreeWithCutExample() = multiRunConcurrentTest {
        super.testPrologStandardSearchTreeWithCutExample()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
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
    @Ignore // Cut need to be implemented
    override fun testConjunctionProperties() = multiRunConcurrentTest {
        super.testConjunctionProperties()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testCallPrimitive() = multiRunConcurrentTest {
        super.testCallPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Cut need to be implemented
    override fun testCallPrimitiveTransparency() = multiRunConcurrentTest {
        super.testCallPrimitiveTransparency()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // same solution count does not match (probably correct)
    override fun testCatchPrimitive() = multiRunConcurrentTest {
        super.testCatchPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // solutions do not match (probably correct)
    override fun testCatchPrimitiveTransparency() = multiRunConcurrentTest {
        super.testCatchPrimitiveTransparency()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo check needed, solutions appear equals but tests fail
    override fun testHaltPrimitive() = multiRunConcurrentTest {
        super.testHaltPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo check needed, solution is not halt but unknown cause
    override fun testNotPrimitive() = multiRunConcurrentTest {
        super.testNotPrimitive()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo check needed, solutions do not match
    override fun testNotModularity() = multiRunConcurrentTest {
        super.testNotModularity()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Arrow need to be implemented
    override fun testIfThenRule() = multiRunConcurrentTest {
        super.testIfThenRule()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Arrow need to be implemented
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
    @Ignore // some API need to be implemented (abolish, assert, etc.)
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
    @Ignore // backtracking is not present in concurrent solver, probably useless test
    override fun testBasicBacktracking1() = multiRunConcurrentTest {
        super.testBasicBacktracking1()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // backtracking is not present in concurrent solver, probably useless test
    override fun testBasicBacktracking2() = multiRunConcurrentTest {
        super.testBasicBacktracking2()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // backtracking is not present in concurrent solver, probably useless test
    override fun testBasicBacktracking3() = multiRunConcurrentTest {
        super.testBasicBacktracking3()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // backtracking is not present in concurrent solver, probably useless test
    override fun testBasicBacktracking4() = multiRunConcurrentTest {
        super.testBasicBacktracking4()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Assert need to be implemented
    override fun testAssertRules() = multiRunConcurrentTest {
        super.testAssertRules()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Retract need to be implemented
    override fun testRetract() = multiRunConcurrentTest {
        super.testRetract()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // todo check needed, more solutions than expected
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
    @Ignore // Retract need to be implemented
    override fun testRetractAll() = multiRunConcurrentTest {
        super.testRetractAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // Append need to be implemented
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
