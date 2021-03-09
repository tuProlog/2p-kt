package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBigList
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogBigList : TestBigList, SolverFactory by ProblogSolverFactory {
    private val prototype = TestBigList.prototype(this)

    @Test
    override fun testBigListGeneration() {
        prototype.testBigListGeneration()
    }
}
