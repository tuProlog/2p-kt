package it.unibo.tuprolog.solve

interface TestStaticFactory {

    data class Expectations(
        val classicShouldWork: Boolean = false,
        val streamsShouldWork: Boolean = false,
        val prologShouldWork: Boolean = false,
        val problogShouldWork: Boolean = false,
    )

    companion object {
        fun prototype(expectations: Expectations): TestStaticFactory = TestStaticFactoryImpl(expectations)
    }

    fun testStaticSolverFactoryForClassic()

    fun testStaticSolverFactoryForStreams()

    fun testStaticSolverFactoryForProlog()

    fun testStaticSolverFactoryForProblog()
}
