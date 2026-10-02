package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomCodes
import kotlin.test.Test

class TestPrologAtomCodes :
    TestAtomCodes,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestAtomCodes.prototype(this)

    @Test
    override fun testAtomCodesSecondIsVar1() {
        prototype.testAtomCodesSecondIsVar1()
    }

    @Test
    override fun testAtomCodesSecondIsVar() {
        prototype.testAtomCodesSecondIsVar()
    }

    @Test
    override fun testAtomCodesFirstIsVar() {
        prototype.testAtomCodesFirstIsVar()
    }

    @Test
    override fun testAtomCodesNoVar() {
        prototype.testAtomCodesNoVar()
    }

    @Test
    override fun testAtomCodesFail() {
        prototype.testAtomCodesFail()
    }
}
