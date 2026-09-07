package it.unibo.tuprolog.solve

/**
 * Conformance tests for `Solver.solve(goal, maxDuration)`'s timeout enforcement: checking that a computation
 * exceeding its allotted [it.unibo.tuprolog.solve.TimeDuration] halts with a
 * [it.unibo.tuprolog.solve.exception.TimeOutException], both for a plainly slow goal (`sleep/1`) and for
 * non-terminating all-solutions collectors (`findall/3`, `bagof/3`, `setof/3`) fed by an infinite generator. Shared
 * by every `Solver` implementation via the `TestTimeout.prototype(solverFactory)` factory (see
 * `TestClassicTimeout` in `:solve-classic` for a concrete usage).
 */
interface TestTimeout : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTimeout = TestTimeoutImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- sleep(MediumDuration).
     * ```
     * solved with [shortDuration] as the max duration: since the sleep alone exceeds the budget, the sole result is
     * a [Solution.Halt] wrapping a [it.unibo.tuprolog.solve.exception.TimeOutException] for [shortDuration].
     */
    fun testSleep()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(N, nat(N), L).
     * ```
     * against a theory recursively defining the (infinite) natural numbers `nat(z). nat(s(Z)) :- nat(Z).`, solved
     * with [shortDuration] as the max duration: since `findall/3` must exhaust every solution of its generator
     * before returning, and the generator never terminates, the sole result is a [Solution.Halt] wrapping a
     * [it.unibo.tuprolog.solve.exception.TimeOutException] for [shortDuration].
     */
    fun testInfiniteFindAll()

    /** Same as [testInfiniteFindAll], but with `bagof(N, nat(N), L)` in place of `findall/3`. */
    fun testInfiniteBagOf()

    /** Same as [testInfiniteFindAll], but with `setof(N, nat(N), L)` in place of `findall/3`. */
    fun testInfiniteSetOf()
}
