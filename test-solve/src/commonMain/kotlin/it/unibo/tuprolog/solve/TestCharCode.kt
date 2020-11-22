package it.unibo.tuprolog.solve

interface TestCharCode : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCharCodeImpl =
            TestCharCodeImpl(solverFactory)
    }

    /**
     * char_code Testing
     *
     * Contained requests:
     * ```prolog
     * ?- char_code(a,X).
     * ?- char_code(X,97).
     * ?- char_code(X,a).
     * ?- char_code(g,104).
     * ```
     */

    fun testCharCodeSecondIsVar()

    fun testCharCodeFirstIsVar()

    fun testCharCodeTypeError()

    fun testCharCodeFails()

}