package it.unibo.tuprolog.solve

interface TestOr {

    companion object{
        fun prototype(solverFactory: SolverFactory): TestOr =
                TestOrImpl(solverFactory)
    }

    /** A short test max duration */
    val shortDuration: TimeDuration
        get() = 250L

    /** A medium test max duration */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration */
    val longDuration: TimeDuration
        get() = 4 * mediumDuration

    /** Test presence of correct built-ins */
    fun testTrueOrFalse()

    fun testCutFalseOrTrue()

    fun testCutCall()

    fun testCutAssignedValue()

    fun testOrDoubleAssignment()
}