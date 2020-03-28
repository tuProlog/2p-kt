package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StreamsSolverSystemTesting : SolverFactory {

    /** A short test max duration */
    private val shortDuration = 500L
    /** A medium test max duration */
    private val mediumDuration = 1000L
    /** A long test max duration */
    private val longDuration = 2000L

    private val prototype = SolverTestPrototype(this)

    override val defaultLibraries: Libraries = Libraries(DefaultBuiltins)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: ClauseDatabase,
        dynamicKb: ClauseDatabase,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver = Solver.streams(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: ClauseDatabase,
        dynamicKb: ClauseDatabase,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): MutableSolver = MutableSolver.streams(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    @Test
    @Ignore
    fun entryPointForManualTests() {
        prolog {
            val solver = solverOf(
                libraries = defaultLibraries
            )

            val prints = mutableListOf<String>()

            solver.standardOutput?.addListener { prints.add(it) }

            val query = "write"("hello world") and "nl" and "assert"("a") and "true" and "a"

            solver.solve(query).forEach(::println)

            assertEquals("hello world", prints[0])
            assertEquals("\n", prints[1])
            assertTrue {
                solver.dynamicKb.contains(atomOf("a"))
            }
        }
    }

    @Test
    fun testAssert() {
        prototype.testAssert()
    }

    @Test
    fun testAssertZ() {
        prototype.testAssertZ()
    }

    @Test
    @Ignore
    fun testAssertA() {
        prototype.testAssertA()
    }

    @Test
    fun testWrite() {
        prototype.testWrite()
    }

    @Test
    fun testStandardOutput() {
        prototype.testStandardOutput()
    }

    @Test
    fun testTrue() {
        prototype.testTrue()
    }

    @Test
    @Ignore
    fun testIfThen1() {
        prototype.testIfThen1()
    }

    @Test
    @Ignore
    fun testIfThen2() {
        prototype.testIfThen2()
    }

    @Test
    @Ignore
    fun testIfThenElse1() {
        prototype.testIfThenElse1()
    }

    @Test
    @Ignore
    fun testIfThenElse2() {
        prototype.testIfThenElse2()
    }


    @Test
    fun testTimeout1() {
        prototype.testTimeout1()
    }

    @Test
    fun testTimeout2() {
        prototype.testTimeout2()
    }

    @Test
    fun testTimeout3() {
        prototype.testTimeout3()
    }

    @Test
    fun testTimeout4() {
        prototype.testTimeout4()
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
