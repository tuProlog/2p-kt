package it.unibo.tuprolog.solve

/**
 * Conformance tests for how a resolved [Solution]'s substitution treats variables that do not directly appear in
 * the original query ("uninteresting" variables, introduced only by intermediate goals/clause bodies), and for the
 * [it.unibo.tuprolog.solve.flags.TrackVariables] flag that controls whether they are kept visible to a custom
 * primitive during resolution. Shared by every `Solver` implementation via the
 * `TestSubstitutions.prototype(solverFactory)` factory (see `TestClassicSubstitutions` in `:solve-classic` for a
 * concrete usage).
 *
 * The last two test cases use a custom `inspect/1` primitive (registered against
 * [TestingClauseTheories.callsWithVariablesAndInspectorTheory]) that records, at each of its four call sites, the
 * names of every variable of its argument still bound to (or reachable from) the *query's own* substitution — i.e.
 * what a nested primitive can actually observe of the caller's variables while resolution is in progress.
 */
interface TestSubstitutions : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSubstitutions = TestSubstitutionsImpl(solverFactory)
    }

    /**
     * Runs [interestingVariablesAreProperlyTracked]'s scenario with
     * [it.unibo.tuprolog.solve.flags.TrackVariables] `off`, and checks that the query's own variables (`A`, `B`,
     * `C`) are still observed by the inspector at every call site regardless — only the *intermediate* variable
     * `D` (introduced by `callsWithVariablesAndInspectorTheory`'s own rule bodies) is affected by the flag.
     */
    fun interestingVariablesAreNotObliterated()

    /**
     * Solves `p(A, B, C)` against [TestingClauseTheories.callsWithVariablesAndInspectorTheory]'s theory with
     * [it.unibo.tuprolog.solve.flags.TrackVariables] `on`; the intermediate variable `D` (bound only inside the
     * theory's own rule bodies, not directly by the query) is observed by the inspector at its fourth call site,
     * alongside the query's own `A`/`B`/`C`/`E` — it is not discarded even though it is not one of the query's
     * variables.
     */
    fun interestingVariablesAreProperlyTracked()

    /**
     * Solves `a(X)` against [TestingClauseTheories.callsWithVariablesTheory] (`a(A) :- b(A), d(Z).` and similar
     * rules introducing intermediate variables `Z`/`W`); succeeds with `X` bound to `1`, and the resulting
     * substitution contains only `X` — the intermediate variables introduced along the way are obliterated from
     * the final substitution once they are no longer needed.
     */
    fun uninterestingVariablesAreObliterated()
}
