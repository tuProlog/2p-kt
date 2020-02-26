package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

class StreamsSolverSystemTesting : SolverFactory {

    /** A short test max duration */
    private val shortDuration = 100L
    /** A medium test max duration */
    private val mediumDuration = 500L
    /** A long test max duration */
    private val longDuration = 2000L

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf( // TODO: 17/01/2020 add not present tests 
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase
    ): Solver = StreamsSolver(libraries, flags, staticKB, dynamicKB)

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification(shortDuration)
    }

    @Test
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives(shortDuration)
    }

    @Test
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction(shortDuration)
    }

    @Test
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking(shortDuration)
    }

    @Test
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException(shortDuration)
    }

    @Test
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample(shortDuration)
    }

    @Test
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample(shortDuration)
    }

    @Test
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation(shortDuration)
    }

    @Test
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples(shortDuration)
    }

    @Test
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties(shortDuration)
    }

    @Test
    fun testCallPrimitive() {
        prototype.testCallPrimitive(shortDuration)
    }

    @Test
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency(shortDuration)
    }

    @Test
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive(shortDuration)
    }

    @Test
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency(shortDuration)
    }

    @Test
    fun testHaltPrimitive() {
        prototype.testHaltPrimitive(shortDuration)
    }

    @Test
    fun testNotPrimitive() {
        prototype.testNotPrimitive(shortDuration)
    }

    @Test
    fun testNotModularity() {
        prototype.testNotModularity(shortDuration)
    }

    @Test
    fun testIfThenRule() {
        prototype.testIfThenRule(shortDuration)
    }

    @Test
    fun testIfTheElseRule() {
        prototype.testIfThenElseRule(shortDuration)
    }

    @Test
    fun testNumbersRangeListGeneration() {
        prototype.testNumbersRangeListGeneration(shortDuration)
    }

    @Test
    fun testConjunction() {
        prototype.testConjunction(shortDuration)
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification(shortDuration)
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction(shortDuration)
    }

    @Test
    fun testFailure() {
        prototype.testFailure(shortDuration)
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification(shortDuration)
    }

    @Test
    fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions(shortDuration)
    }

    @Test
    fun testMember() {
        prototype.testMember(shortDuration)
    }

    @Test
    fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1(shortDuration)
    }

    @Test
    fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2(shortDuration)
    }

    @Test
    fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3(shortDuration)
    }

    @Test
    fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4(shortDuration)
    }
}
