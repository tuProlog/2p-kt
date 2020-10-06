package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtom
import kotlin.test.Test

class TestClassicAtom : TestAtom, SolverFactory by ClassicSolverFactory {

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

    @Test
    override fun testAtomChars() {
        prototype.testAtomChars()
    }

    @Test
    override fun testAtomCodes() {
        prototype.testAtomCodes()
    }

    @Test
    override fun testAtomConcat() {
        prototype.testAtomConcat()
    }

    @Test
    override fun testAtomLength() {
        prototype.testAtomLength()
    }

    @Test
    override fun testCharCode() {
        prototype.testCharCode()
    }
}
