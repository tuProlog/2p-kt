package it.unibo.tuprolog.solve

/**
 * Conformance tests for `Solver.classic`/`Solver.streams`/`Solver.prolog`/`Solver.problog`, the static factory
 * properties on [Solver]'s companion object that locate a concrete [SolverFactory] implementation at runtime (via a
 * service-loading mechanism) without the caller needing a compile-time dependency on the implementing module.
 * Since a given caller module only bundles some of those implementations, which ones are expected to succeed vs.
 * fail is passed in as an [Expectations] instance rather than hard-coded. Instantiated (not via a `solverFactory`,
 * unlike every other `TestXxx` interface in this module) via `TestStaticFactory.prototype(expectations)`; see
 * `SolveClassicTest`/`SolveConcurrentTest` (in `:solve-classic`/`:solve-concurrent`) for concrete usages.
 *
 * Unlike the other `TestXxx` interfaces here, this one does not extend [SolverTest]: it never resolves a goal, so
 * it needs no `solve` timeout.
 */
interface TestStaticFactory {
    companion object {
        fun prototype(expectations: Expectations): TestStaticFactory = TestStaticFactoryImpl(expectations)
    }

    /**
     * If [Expectations.classicShouldWork], tests that `Solver.classic` (and a solver/builder obtained from it)
     * resolve to `it.unibo.tuprolog.solve.classic.ClassicSolver`; otherwise, tests that looking it up throws
     * `IllegalStateException` (or is otherwise unavailable, platform permitting — see the JS caveat in the
     * implementation).
     */
    fun testStaticSolverFactoryForClassic()

    /** Same as [testStaticSolverFactoryForClassic], but for `Solver.streams` / [Expectations.streamsShouldWork],
     * expecting `it.unibo.tuprolog.solve.streams.StreamsSolver`. */
    fun testStaticSolverFactoryForStreams()

    /**
     * Same as [testStaticSolverFactoryForClassic], but for `Solver.prolog` / [Expectations.prologShouldWork];
     * `Solver.prolog` is currently an alias resolving to the same classic implementation.
     */
    fun testStaticSolverFactoryForProlog()

    /** Same as [testStaticSolverFactoryForClassic], but for `Solver.problog` / [Expectations.problogShouldWork],
     * expecting `it.unibo.tuprolog.solve.problog.ProblogSolver`. */
    fun testStaticSolverFactoryForProblog()
}
