package it.unibo.tuprolog.solve

/**
 * Tells [TestStaticFactory] which of [Solver]'s static factory properties (`Solver.classic`, `Solver.streams`,
 * `Solver.prolog`, `Solver.problog`) are expected to succeed in the module/platform combination under test.
 *
 * Each `Solver.xxx` static factory locates its implementation via a runtime service-loading mechanism, so it only
 * works when the corresponding implementation module (e.g. `:solve-classic`) is actually on the classpath/bundle of
 * the module running the test; a module that does not depend on `:solve-streams`, for instance, must build its
 * `Expectations` with `streamsShouldWork = false` so [TestStaticFactory] asserts the lookup fails there instead of
 * succeeding. See `SolveClassicTest`/`SolveConcurrentTest` (in `:solve-classic`/`:solve-concurrent`) for concrete
 * instantiations, each setting only the flags true for the factories it actually bundles.
 *
 * [concurrentShouldWork] is set by `SolveConcurrentTest` but, as of this writing, [TestStaticFactory] has no
 * corresponding `testStaticSolverFactoryForConcurrent()` test case that reads it — there is no `Solver.concurrent`
 * static factory property to check.
 */
data class Expectations(
    val classicShouldWork: Boolean = false,
    val streamsShouldWork: Boolean = false,
    val prologShouldWork: Boolean = false,
    val problogShouldWork: Boolean = false,
    val concurrentShouldWork: Boolean = false,
)
