package it.unibo.tuprolog.solve

import kotlin.test.Test

class TestPrologStaticFactory : TestStaticFactory {
    private val prototype =
        TestStaticFactory.prototype(
            SolvePrologTest.expectations,
        )

    @Test
    override fun testStaticSolverFactoryForProlog() {
        prototype.testStaticSolverFactoryForProlog()
    }

    @Test
    override fun testStaticSolverFactoryForProblog() {
        prototype.testStaticSolverFactoryForProblog()
    }
}
