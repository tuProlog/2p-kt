package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class ClassicSolverTestArg : TestArg, SolverFactory {

    private val prototype = TestArg.prototype(this)

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
    override fun testArgFromFoo(){
        prototype.testArgFromFoo()
    }

    @Test
    //@Ignore
    override fun testArgFromFooX(){
        prototype.testArgFromFooX()
    }

    @Test
    override fun testArgFromFoo2(){
        prototype.testArgFromFoo2()
    }

    @Test
    override fun testArgFromFooInF(){
        prototype.testArgFromFooInF()
    }

    @Test
    override fun testArgFromFooY(){
        prototype.testArgFromFooY()
    }

    @Test
    override fun testArgFromFooInSecondTerm(){
        prototype.testArgFromFooInSecondTerm()
    }

    @Test
    override fun testArgFromFooInFoo(){
        prototype.testArgFromFooInFoo()
    }

    @Test
    override fun testArgNumberFromFoo(){
        prototype.testArgNumberFromFoo()
    }

    @Test
    override fun testArgXFromFoo(){
        prototype.testArgXFromFoo()
    }

    @Test
    override fun testArgNumberFromX(){
        prototype.testArgNumberFromX()
    }

    @Test
    override fun testArgFromAtom(){
        prototype.testArgFromAtom()
    }

    @Test
    override fun testArgFromNumber(){
        prototype.testArgFromNumber()
    }

    @Test
    override fun testNegativeArgFromFoo(){
        prototype.testNegativeArgFromFoo()
    }

    @Test
    override fun testArgAFromFoo(){
        prototype.testArgAFromFoo()
    }
}