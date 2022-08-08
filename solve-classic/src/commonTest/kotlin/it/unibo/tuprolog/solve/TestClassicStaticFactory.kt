package it.unibo.tuprolog.solve

import kotlin.test.Test

class TestClassicStaticFactory : TestStaticFactory {
    private val prototype = TestStaticFactory.prototype(
        SolveClassicTest.expectations
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
