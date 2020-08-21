package it.unibo.tuprolog.solve

/** A prototype class for testing solver implementations */
interface TestNonVar {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestNonVar =
                TestNonVarImpl(solverFactory)
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
    fun testNonVarNumber()

    fun testNonVarFoo()

    fun testNonVarFooCl()

    fun testNonVarFooAssignment()

    fun testNonVarAnyTerm()

    fun testNonVar()
}