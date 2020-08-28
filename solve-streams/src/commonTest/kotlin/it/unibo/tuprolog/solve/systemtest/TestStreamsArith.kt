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

class TestStreamsArith : TestArith, SolverFactory by StreamsSolverFactory {
    private val prototype = TestArith.prototype(this)

    @Test
    override fun testArithDiff() {
        prototype.testArithDiff()
    }

    @Test
    override fun testArithEq() {
        prototype.testArithEq()
    }

    @Test
    override fun testArithGreaterThan() {
        prototype.testArithGreaterThan()
    }

    @Test
    override fun testArithGreaterThanEq() {
        prototype.testArithGreaterThanEq()
    }

    @Test
    override fun testArithLessThan() {
        prototype.testArithLessThan()
    }

    @Test
    override fun testArithLessThanEq() {
        prototype.testArithLessThanEq()
    }
}

