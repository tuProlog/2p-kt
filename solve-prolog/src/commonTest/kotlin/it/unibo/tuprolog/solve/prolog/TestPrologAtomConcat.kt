package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomConcat
import kotlin.test.Test

class TestPrologAtomConcat :
    TestAtomConcat,
    SolverFactory by PrologSolverFactory {
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
    override fun testAtomConcatFailsNotPrefix() {
        prototype.testAtomConcatFailsNotPrefix()
    }

    @Test
    override fun testAtomConcatFailsNotSuffix() {
        prototype.testAtomConcatFailsNotSuffix()
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
