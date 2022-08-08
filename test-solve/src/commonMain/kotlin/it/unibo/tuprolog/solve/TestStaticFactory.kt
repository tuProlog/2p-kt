package it.unibo.tuprolog.solve

interface TestStaticFactory {

    companion object {
        fun prototype(expectations: Expectations): TestStaticFactory = TestStaticFactoryImpl(expectations)
    }

    fun testStaticSolverFactoryForClassic()

    fun testStaticSolverFactoryForStreams()

    fun testStaticSolverFactoryForProlog()

    fun testStaticSolverFactoryForProblog()
}
