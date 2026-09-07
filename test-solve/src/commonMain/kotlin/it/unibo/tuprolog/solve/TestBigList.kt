package it.unibo.tuprolog.solve

/**
 * A stress test that recursively builds a list of [BigListOptions.SIZE] elements (via a hand-written `biglist/2`
 * predicate) and checks the resulting list is correct, to catch performance regressions or stack-depth issues that
 * only manifest on deep/large resolutions. Shared by every `Solver` implementation via the
 * `TestBigList.prototype(solverFactory)` factory (see `TestClassicBigList` in `:solve-classic` for a concrete usage).
 * [BigListOptions.SIZE] is tuned per platform (smaller on JS, where recursion is costlier).
 */
interface TestBigList : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestBigList = TestBigListImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- biglist(N, L).
     * ```
     * against a theory recursively defining `biglist(N, [N|Rest])` down to `biglist(0, [0])`, with `N` bound to
     * [BigListOptions.SIZE]; succeeds producing 1 solution which binds `L` to the list `[N, N-1, ..., 1, 0]`,
     * followed by a `no` (no further solutions on backtracking).
     */
    fun testBigListGeneration()
}
