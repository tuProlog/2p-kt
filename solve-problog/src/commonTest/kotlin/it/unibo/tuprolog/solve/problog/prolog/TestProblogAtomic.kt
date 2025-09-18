package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomic
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAtomic :
    TestAtomic,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAtomic.prototype(this)

    @Test
    override fun testAtomicAtom() {
        prototype.testAtomicAtom()
    }

    @Test
    override fun testAtomicAofB() {
        prototype.testAtomicAofB()
    }

    @Test
    override fun testAtomicVar() {
        prototype.testAtomicVar()
    }

    @Test
    override fun testAtomicEmptyList() {
        prototype.testAtomicEmptyList()
    }

    @Test
    override fun testAtomicNum() {
        prototype.testAtomicNum()
    }

    @Test
    override fun testAtomicNumDec() {
        prototype.testAtomicNumDec()
    }
}
