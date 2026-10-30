package it.unibo.tuprolog.solve

/**
 * Tells [staticFactoryContract] which of [Solver]'s static factory properties (`Solver.prolog`, `Solver.problog`) are
 * expected to succeed in the module/platform combination under test.
 *
 * Each `Solver.xxx` static factory locates its implementation via a runtime service-loading mechanism, so it only
 * works when the corresponding implementation module (e.g. `:solve-prolog`) is actually on the classpath/bundle of
 * the module running the test. See `SolvePrologExpectations`/`SolveConcurrentExpectations` (in `:solve-prolog`/`:solve-concurrent`) for concrete
 * instantiations, each setting only the flags true for the factories it actually bundles.
 *
 * [concurrentShouldWork] is set by `SolveConcurrentExpectations` but, as of this writing, [staticFactoryContract] has no
 * corresponding `testStaticSolverFactoryForConcurrent()` test case that reads it — there is no `Solver.concurrent`
 * static factory property to check.
 */
data class Expectations(
    val prologShouldWork: Boolean = false,
    val problogShouldWork: Boolean = false,
    val concurrentShouldWork: Boolean = false,
)
