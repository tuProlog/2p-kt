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

class ClassicSolverTestAnd : TestAnd, SolverFactory {

    private val prototype = TestAnd.prototype(this)

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
            libraries: Libraries,
            flags: PrologFlags,
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
            flags: PrologFlags,
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
    override fun test1() {
        prototype.test1()
    }

    @Test
    override fun test2() {
        prototype.test2()
    }

    @Test
    override fun test3() {
        prototype.test3()
    }
}