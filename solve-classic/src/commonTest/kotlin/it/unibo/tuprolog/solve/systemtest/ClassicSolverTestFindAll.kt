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

class ClassicSolverTestFindAll : TestFindAll, SolverFactory {

    private val prototype = TestFindAll.prototype(this)

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
    override fun testFindXInDiffValues(){
        prototype.testFindXInDiffValues()
    }

    @Test
    override fun testFindSumResult(){
        prototype.testFindSumResult()
    }

    @Test
    override fun testFindXinFail(){
        prototype.testFindXinFail()
    }

    @Test
//    @Ignore
    override fun testFindXinSameXValues(){
        prototype.testFindXinSameXValues()
    }

    @Test
    @Ignore // TODO @gciatto-unibo must fix this
    override fun testResultListIsCorrect(){
        prototype.testResultListIsCorrect()
    }

    @Test
    @Ignore // TODO @gciatto-unibo must fix this
    override fun testFindXtoDoubleAssigment(){
        prototype.testFindXtoDoubleAssigment()
    }

    @Test
    override fun testFindXinGoal(){
        prototype.testFindXinGoal()
    }

    @Test
    @Ignore // TODO @gciatto-unibo must fix this
    override fun testFindXinNumber(){
        prototype.testFindXinNumber()
    }

    @Test
    @Ignore // TODO @gciatto-unibo must fix this
    override fun testFindXinCall(){
        prototype.testFindXinCall()
    }
}