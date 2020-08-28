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

class TestStreamsOr : TestOr, SolverFactory by StreamsSolverFactory {

    private val prototype = TestOr.prototype(this)

    @Test
    override fun testTrueOrFalse() {
        prototype.testTrueOrFalse()
    }

    @Test
    @Ignore
    override fun testCutFalseOrTrue() {
        prototype.testCutFalseOrTrue()
    }

    @Test
    @Ignore
    override fun testCutCall() {
        prototype.testCutCall()
    }

    @Test
    @Ignore
    override fun testCutAssignedValue() {
        prototype.testCutAssignedValue()
    }

    @Test
    override fun testOrDoubleAssignment() {
        prototype.testOrDoubleAssignment()
    }


}