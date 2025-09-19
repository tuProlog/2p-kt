package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtom
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsAtom :
    TestAtom,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestAtom.prototype(this)

    @Test
    override fun testAtomAtom() {
        prototype.testAtomAtom()
    }

    @Test
    override fun testAtomString() {
        prototype.testAtomString()
    }

    @Test
    override fun testAtomAofB() {
        prototype.testAtomAofB()
    }

    @Test
    override fun testAtomVar() {
        prototype.testAtomVar()
    }

    @Test
    override fun testAtomEmptyList() {
        prototype.testAtomEmptyList()
    }

    @Test
    override fun testAtomNum() {
        prototype.testAtomNum()
    }

    @Test
    override fun testAtomNumDec() {
        prototype.testAtomNumDec()
    }
}
