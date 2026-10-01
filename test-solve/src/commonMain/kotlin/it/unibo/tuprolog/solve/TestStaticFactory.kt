package it.unibo.tuprolog.solve

/**
 * Conformance tests for `Solver.prolog`/`Solver.problog`, the static factory
 * properties on [Solver]'s companion object that locate a concrete [SolverFactory] implementation at runtime (via a
 * service-loading mechanism) without the caller needing a compile-time dependency on the implementing module.
 * Since a given caller module only bundles some of those implementations, which ones are expected to succeed vs.
 * fail is passed in as an [Expectations] instance rather than hard-coded. Instantiated (not via a `solverFactory`,
 * unlike every other `TestXxx` interface in this module) via `TestStaticFactory.prototype(expectations)`; see
 * `SolvePrologTest`/`SolveConcurrentTest` (in `:solve-prolog`/`:solve-concurrent`) for concrete usages.
 *
 * Unlike the other `TestXxx` interfaces here, this one does not extend [SolverTest]: it never resolves a goal, so
 * it needs no `solve` timeout.
 */
interface TestStaticFactory {
    companion object {
        fun prototype(expectations: Expectations): TestStaticFactory = TestStaticFactoryImpl(expectations)
    }

    /**
     * If [Expectations.prologShouldWork], tests that `Solver.prolog` resolves to
     * `it.unibo.tuprolog.solve.prolog.PrologSolver`; otherwise tests that the lookup is unavailable.
     */
    fun testStaticSolverFactoryForProlog()

    /** Same as [testStaticSolverFactoryForProlog], but for `Solver.problog` / [Expectations.problogShouldWork],
     * expecting `it.unibo.tuprolog.solve.problog.ProblogSolver`. */
    fun testStaticSolverFactoryForProblog()
}
