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

class TestStreamsFindAll : TestFindAll, SolverFactory by StreamsSolverFactory {

    private val prototype = TestFindAll.prototype(this)

    @Test
    override fun testFindXInDiffValues() {
        prototype.testFindXInDiffValues()
    }

    @Test
    override fun testFindSumResult() {
        prototype.testFindSumResult()
    }

    @Test
    override fun testFindXinFail() {
        prototype.testFindXinFail()
    }

    @Test
    override fun testFindXinSameXValues() {
        prototype.testFindXinSameXValues()
    }

    @Test
    override fun testResultListIsCorrect() {
        prototype.testResultListIsCorrect()
    }

    @Test
    override fun testFindXtoDoubleAssigment() {
        prototype.testFindXtoDoubleAssigment()
    }

    @Test
    @Ignore
    override fun testFindXinGoal() {
        prototype.testFindXinGoal()
    }

    @Test
    @Ignore
    override fun testFindXinNumber() {
        prototype.testFindXinNumber()
    }

    @Test
    @Ignore
    override fun testFindXinCall() {
        prototype.testFindXinCall()
    }
}