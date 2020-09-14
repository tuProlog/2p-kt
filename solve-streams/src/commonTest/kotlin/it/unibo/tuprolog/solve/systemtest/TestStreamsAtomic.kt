package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestAtomic
import kotlin.test.Test

class TestStreamsAtomic : TestAtomic, SolverFactory by StreamsSolverFactory {

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
