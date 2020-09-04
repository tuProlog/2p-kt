package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOnce
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicOnce : TestOnce, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestOnce.prototype(this)

    @Test
    @Ignore
    override fun testOnceCut() {
        prototype.testOnceCut()
    }

    @Test
    @Ignore
    override fun testOnceCutOr() {
        prototype.testOnceCutOr()
    }

    @Test
    @Ignore
    override fun testOnceRepeat() {
        prototype.testOnceRepeat()
    }

    @Test
    @Ignore
    override fun testOnceFail() {
        prototype.testOnceFail()
    }

    @Test
    @Ignore
    override fun testOnceNum() {
        prototype.testOnceNum()
    }

    @Test
    @Ignore
    override fun testOnceX() {
        prototype.testOnceX()
    }
}