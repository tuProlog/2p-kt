package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestSubAtom
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamSubAtom : TestSubAtom, SolverFactory by StreamsSolverFactory {
    private val prototype = TestSubAtom.prototype(this)

    @Test
    @Ignore
    override fun testSubAtomSubIsVar() {
        prototype.testSubAtomSubIsVar()
    }

    @Test
    @Ignore
    override fun testSubAtomSubIsVar2() {
        prototype.testSubAtomSubIsVar2()
    }

    @Test
    @Ignore
    override fun testSubAtomSubIsVar3() {
        prototype.testSubAtomSubIsVar3()
    }

    @Test
    @Ignore
    override fun testSubAtomDoubleVar4() {
        prototype.testSubAtomDoubleVar4()
    }
}
