package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test

class TestClassicUnify : TestUnify, SolverFactory {

    private val prototype = TestUnify.prototype(this)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: FlagStorage,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = Solver.classic(
            libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings
    )

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: FlagStorage,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = MutableSolver.classic(
            libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings
    )

    @Test
    override fun testNumberUnify() {
        prototype.testNumberUnify()
    }

    @Test
    override fun testNumberXUnify() {
        prototype.testNumberXUnify()
    }

    @Test
    override fun testXYUnify() {
        prototype.testXYUnify()
    }

    @Test
    override fun testDoubleUnify() {
        prototype.testDoubleUnify()
    }

    @Test
    override fun testFDefUnify() {
        prototype.testFDefUnify()
    }

    @Test
    override fun testDiffNumberUnify() {
        prototype.testDiffNumberUnify()
    }

    @Test
    override fun testDecNumberUnify() {
        prototype.testDecNumberUnify()
    }

    @Test
    override fun testGUnifyFX() {
        prototype.testGUnifyFX()
    }

    @Test
    override fun testFUnify() {
        prototype.testFUnify()
    }

    @Test
    override fun testFMultipleTermUnify() {
        prototype.testFMultipleTermUnify()
    }

    @Test
    override fun testMultipleTermUnify() {
        prototype.testMultipleTermUnify()
    }

}