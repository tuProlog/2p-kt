package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBigList
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

// NOTE: Ignored because the heavier stack usage of this implementation makes this not meaningful.
@Ignore
class TestProblogBigList :
    TestBigList,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestBigList.prototype(this)

    @Test
    override fun testBigListGeneration() {
        prototype.testBigListGeneration()
    }
}
