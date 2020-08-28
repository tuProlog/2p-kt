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

class TestClassicNotUnify : TestNotUnify, SolverFactory {

    private val prototype = TestNotUnify.prototype(this)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
            libraries: Libraries,
            flags: FlagStore,
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
            flags: FlagStore,
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
    override fun testNumberNotUnify() {
        prototype.testNumberNotUnify()
    }

    @Test
    override fun testNumberXNotUnify() {
        prototype.testNumberXNotUnify()
    }

    @Test
    override fun testXYNotUnify() {
        prototype.testXYNotUnify()
    }

    @Test
    override fun testDoubleNotUnify() {
        prototype.testDoubleNotUnify()
    }

    @Test
    override fun testFDefNotUnify() {
        prototype.testFDefNotUnify()
    }

    @Test
    override fun testDiffNumberNotUnify() {
        prototype.testDiffNumberNotUnify()
    }

    @Test
    override fun testDecNumberNotUnify() {
        prototype.testDecNumberNotUnify()
    }

    @Test
    override fun testGNotUnifyFX() {
        prototype.testGNotUnifyFX()
    }

    @Test
    override fun testFNotUnify() {
        prototype.testFNotUnify()
    }

    @Test
    override fun testFMultipleTermNotUnify() {
        prototype.testFMultipleTermNotUnify()
    }

}