package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

class SolverSLDSystemTesting : SolverFactory {

    val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(libraries: Libraries, flags: PrologFlags, staticKB: ClauseDatabase, dynamicKB: ClauseDatabase): Solver =
            SolverSLD(libraries, flags, staticKB, dynamicKB)

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification()
    }

    @Test
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives()
    }

    @Test
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction()
    }

    @Test
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking()
    }

    @Test
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException()
    }

    @Test
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample()
    }

    @Test
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample()
    }

    @Test
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation()
    }

    @Test
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples()
    }

    @Test
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties()
    }

    @Test
    fun testCallPrimitive() {
        prototype.testCallPrimitive()
    }

    @Test
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency()
    }

    @Test
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive()
    }

    @Test
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency()
    }

    @Test
    fun testConjunction() {
        prototype.testConjunction()
    }

    @Test
    fun testConjunctionWithUnification() {
        prototype.testConjunctionWithUnification()
    }

    @Test
    fun testBuiltinApi() {
        prototype.testBuiltinApi()
    }

    @Test
    fun testDisjunction() {
        prototype.testDisjunction()
    }

    @Test
    fun testFailure() {
        prototype.testFailure()
    }

    @Test
    fun testDisjunctionWithUnification() {
        prototype.testDisjunctionWithUnification()
    }

    @Test
    fun testConjunctionOfConjunctions() {
        prototype.testConjunctionOfConjunctions()
    }

    @Test
    fun testMember() {
        prototype.testMember()
    }

    @Test
    fun testBasicBacktracking1() {
        prototype.testBasicBacktracking1()
    }

    @Test
    fun testBasicBacktracking2() {
        prototype.testBasicBacktracking2()
    }

    @Test
    fun testBasicBacktracking3() {
        prototype.testBasicBacktracking3()
    }

    @Test
    fun testBasicBacktracking4() {
        prototype.testBasicBacktracking4()
    }
}
