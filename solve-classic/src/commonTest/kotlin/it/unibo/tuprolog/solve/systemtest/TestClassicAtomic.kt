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

class TestClassicAtomic : TestAtomic, SolverFactory{
    private val prototype = TestAtomic.prototype(this)

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
    override fun testAtomicAtom(){
        prototype.testAtomicAtom()
    }

    @Test
    override fun testAtomicAofB(){
        prototype.testAtomicAofB()
    }

    @Test
    override fun testAtomicVar(){
        prototype.testAtomicVar()
    }

    @Test
    override fun testAtomicEmptyList(){
        prototype.testAtomicEmptyList()
    }

    @Test
    override fun testAtomicNum(){
        prototype.testAtomicNum()
    }

    @Test
    override fun testAtomicNumDec(){
        prototype.testAtomicNumDec()
    }
}