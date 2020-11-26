package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestAtomConcat
import kotlin.test.Test

class TestStreamsAtomConcat : TestAtomConcat, SolverFactory by StreamsSolverFactory {

    private val prototype = TestAtomConcat.prototype(this)

    @Test
    override fun testAtomConcatThirdIsVar() {
        prototype.testAtomConcatThirdIsVar()
    }

    @Test
    override fun testAtomConcatFails() {
        prototype.testAtomConcatFails()
    }

    @Test
    override fun testAtomConcatSecondIsVar() {
        prototype.testAtomConcatSecondIsVar()
    }

    @Test
    override fun testAtomConcatFirstIsVar() {
        prototype.testAtomConcatFirstIsVar()
    }
}
