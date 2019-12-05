package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

class ClassicSolverSystemTesting : SolverFactory {

    /** A short test max duration */
    private val shortDuration = 100L
    /** A medium test max duration */
    private val mediumDuration = 500L
    /** A long test max duration */
    private val longDuration = 2000L

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase
    ): Solver = ClassicSolver(libraries, flags, staticKB, dynamicKB)

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification(mediumDuration)
    }

    @Test
    @Ignore // TODO: 07/11/2019 Substitution unused variable cleaning needed to pass this test (issue #52)
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives(mediumDuration)
    }

    @Test
    @Ignore // TODO: (issue #52)
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction(mediumDuration)
    }

    @Test
    @Ignore // TODO: (issue #52)
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking(mediumDuration)
    }

    @Test
    @Ignore // TODO: 08/11/2019 maxDuration parameter not honored, implement correctly that feature (issue #53)
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException(shortDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCallPrimitive() {
        prototype.testCallPrimitive(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive(mediumDuration)
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency(mediumDuration)
    }

    @Test
    fun testHaltPrimitive() {
        prototype.testHaltPrimitive(mediumDuration)
    }

    @Test
    @Ignore // TODO (issue #53)
    fun testNotPrimitive() {
        prototype.testNotPrimitive(mediumDuration)
    }

    @Test
    @Ignore // TODO (issue #53, #52)
    fun testNotModularity() {
        prototype.testNotModularity(mediumDuration)
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testIfThenRule() {
        prototype.testIfThenRule(mediumDuration)
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testIfTheElseRule() {
        prototype.testIfThenElseRule(mediumDuration)
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testNumbersRangeListGeneration() {
        prototype.testNumbersRangeListGeneration(mediumDuration)
    }

    @Test
    fun testConjunction() {
        prototype.testConjunction(mediumDuration)
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification(mediumDuration)
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction(mediumDuration)
    }

    @Test
    fun testFailure() {
        prototype.testFailure(mediumDuration)
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification(mediumDuration)
    }

    @Test
    fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions(mediumDuration)
    }

    @Test
    fun testMember() {
        prototype.testMember(mediumDuration)
    }

    @Test
    fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1(mediumDuration)
    }

    @Test
    fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2(mediumDuration)
    }

    @Test
    fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3(mediumDuration)
    }

    @Test
    fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4(mediumDuration)
    }
}