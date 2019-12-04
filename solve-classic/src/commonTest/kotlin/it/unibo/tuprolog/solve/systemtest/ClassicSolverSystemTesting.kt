package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

class ClassicSolverSystemTesting : SolverFactory {

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKB: ClauseDatabase,
        dynamicKB: ClauseDatabase
    ): Solver = ClassicSolver(libraries, flags, staticKB, dynamicKB)

    @Test
    @Ignore
    fun entryPointForManualTests() {
        prolog {
            val solver = Solver.classic(
                staticKB = theoryOf(
                    rule { "f"("X") impliedBy "f"("X") }
                )
            )

            solver.solve("f"("X"), 1000).forEach {
                println(it)
            }
        }
    }

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

    @Test // TODO (issue #52)
    @Ignore
    fun testCallPrimitive() {
        prototype.testCallPrimitive()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCallPrimitiveTransparency() {
        prototype.testCallPrimitiveTransparency()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCatchPrimitive() {
        prototype.testCatchPrimitive()
    }

    @Test // TODO (issue #52)
    @Ignore
    fun testCatchPrimitiveTransparency() {
        prototype.testCatchPrimitiveTransparency()
    }

    @Test
    fun testHaltPrimitive() {
        prototype.testHaltPrimitive()
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testNotPrimitive() {
        prototype.testNotPrimitive()
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testNotModularity() {
        prototype.testNotModularity()
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testIfThenRule() {
        prototype.testIfThenRule()
    }

    @Test
    @Ignore // TODO (issue #52)
    fun testIfTheElseRule() {
        prototype.testIfThenElseRule()
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