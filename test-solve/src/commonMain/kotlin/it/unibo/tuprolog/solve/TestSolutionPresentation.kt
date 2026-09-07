package it.unibo.tuprolog.solve

/**
 * Conformance tests for how a [Solution]'s substitution presents variables that only occur nested inside another
 * variable's binding (i.e. do not directly correspond to a variable of the original query), rather than for the
 * resolution logic itself. Shared by every `Solver` implementation via the
 * `TestSolutionPresentation.prototype(solverFactory)` factory (see `TestClassicSolutionPresentation` in
 * `:solve-classic` for a concrete usage).
 */
interface TestSolutionPresentation : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSolutionPresentation =
            TestSolutionPresentationImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- append(A, B).
     * ```
     * against the fact `append(seq(X), X).`; succeeds binding `A` to `seq(X)` (a fresh renaming of the fact's own
     * `X`) and `B` to that same fresh variable, and checks that both `A` and `B` are present in the returned
     * substitution's keys — including `B`, whose "value" is itself a dangling variable (named `X`) rather than a
     * ground term.
     */
    fun testSolutionWithDandlingVars()
}
