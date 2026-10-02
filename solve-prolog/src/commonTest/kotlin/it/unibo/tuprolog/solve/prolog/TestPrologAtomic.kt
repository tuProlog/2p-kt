package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomic
import kotlin.test.Test

class TestPrologAtomic :
    TestAtomic,
    SolverFactory by PrologSolverFactory {
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
