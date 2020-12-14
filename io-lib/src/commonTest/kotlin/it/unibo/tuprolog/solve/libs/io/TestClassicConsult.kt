package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import kotlin.test.Test

class TestClassicConsult : TestConsult, SolverFactory by ClassicSolverFactory {
    private val prototype = TestConsult.prototype(this)

    @Test
    override fun testConsultWorksLocally() {
        prototype.testConsultWorksLocally()
    }

    @Test
    override fun testConsultWorksRemotely() {
        prototype.testConsultWorksRemotely()
    }
}
