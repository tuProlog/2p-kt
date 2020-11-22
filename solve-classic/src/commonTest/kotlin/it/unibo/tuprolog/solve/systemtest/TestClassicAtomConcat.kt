package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomConcat
import kotlin.test.Test

class TestClassicAtomConcat : TestAtomConcat, SolverFactory by ClassicSolverFactory {
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
