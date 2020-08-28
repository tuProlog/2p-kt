package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.SolverTest

interface TestCut : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCut =
            TestCutImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- !.
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCut()

    /**
     * Tests the queries
     * ```prolog
     * ?- (!,fail;true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCutFailTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- (call(!),fail;true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCallCutFailTrue()
}