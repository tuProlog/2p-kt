package it.unibo.tuprolog.solve

import kotlin.test.Test

class TestConcurrentStaticFactory : TestStaticFactory {
    private val prototype = TestStaticFactory.prototype(
        SolveConcurrentTest.expectations
    )

    @Test
    override fun testStaticSolverFactoryForClassic() {
        prototype.testStaticSolverFactoryForClassic()
    }

    @Test
    override fun testStaticSolverFactoryForStreams() {
        prototype.testStaticSolverFactoryForStreams()
    }

    @Test
    override fun testStaticSolverFactoryForProlog() {
        prototype.testStaticSolverFactoryForProlog()
    }

    @Test
    override fun testStaticSolverFactoryForProblog() {
        prototype.testStaticSolverFactoryForProblog()
    }
}
