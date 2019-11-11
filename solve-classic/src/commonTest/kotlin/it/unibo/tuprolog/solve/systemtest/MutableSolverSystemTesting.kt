package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

class MutableSolverSystemTesting : SolverFactory {

    val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(libraries: Libraries, flags: PrologFlags, staticKB: ClauseDatabase, dynamicKB: ClauseDatabase): Solver =
            MutableSolver(libraries, flags, staticKB, dynamicKB)

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    fun testUnification() {
        prototype.testUnification()
    }

    @Test
    @Ignore // TODO: 07/11/2019 Substitution unused variable cleaning needed to pass this test (issue #52)
    fun testSimpleCutAlternatives() {
        prototype.testSimpleCutAlternatives()
    }

    @Test
    @Ignore // TODO: (issue #52)
    fun testCutAndConjunction() {
        prototype.testCutAndConjunction()
    }

    @Test
    @Ignore // TODO: (issue #52)
    fun testCutConjunctionAndBacktracking() {
        prototype.testCutConjunctionAndBacktracking()
    }

    @Test
    @Ignore // TODO: 08/11/2019 maxDuration parameter not honored, implement correctly that feature (issue #53)
    fun testMaxDurationParameterAndTimeOutException() {
        prototype.testMaxDurationParameterAndTimeOutException()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testPrologStandardSearchTreeExample() {
        prototype.testPrologStandardSearchTreeExample()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testPrologStandardSearchTreeWithCutExample() {
        prototype.testPrologStandardSearchTreeWithCutExample()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testBacktrackingWithCustomReverseListImplementation() {
        prototype.testBacktrackingWithCustomReverseListImplementation()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testWithPrologStandardConjunctionExamples() {
        prototype.testWithPrologStandardConjunctionExamples()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testConjunctionProperties() {
        prototype.testConjunctionProperties()
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