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

class TestClassicAtom : TestAtom, SolverFactory{
    private val prototype = TestAtom.prototype(this)

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
    override fun testAtomAtom(){
        prototype.testAtomAtom()
    }

    @Test
    override fun testAtomString(){
        prototype.testAtomString()
    }

    @Test
    override fun testAtomAofB(){
        prototype.testAtomAofB()
    }

    @Test
    override fun testAtomVar(){
        prototype.testAtomVar()
    }

    @Test
    override fun testAtomEmptyList(){
        prototype.testAtomEmptyList()
    }

    @Test
    override fun testAtomNum(){
        prototype.testAtomNum()
    }

    @Test
    override fun testAtomNumDec(){
        prototype.testAtomNumDec()
    }
}