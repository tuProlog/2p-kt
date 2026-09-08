package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.library.Library

/**
 * Conformance tests for [SolverFactory]'s two construction APIs — the direct `solverOf`/`mutableSolverOf`/
 * `solverWithDefaultBuiltins`/`mutableSolverWithDefaultBuiltins` factory methods, and the fluent builder returned by
 * [SolverFactory.newBuilder] — checking that, in every combination of (empty vs. custom-configured) and (with vs.
 * without default built-ins) and (immutable vs. mutable), the resulting [Solver]/[MutableSolver] ends up with
 * exactly the expected libraries, theories, flags, and I/O channels (see `Solver.assertHas` in `TestUtils.kt`), and
 * is an instance of the concrete class the caller expects. Shared by every `Solver` implementation via the
 * `TestSolverConstruction.prototype(factory, defaultBuiltins)` factory (see `TestClassicSolverConstruction` in
 * `:solve-classic` for a concrete usage).
 *
 * Unlike the other `TestXxx` interfaces in this module, this one is generic over [T] (the concrete immutable
 * `Solver` class expected, e.g. `ClassicSolver`) and [MT] (its mutable counterpart), which the `prototype` factory
 * captures reified so that every test case can assert the built solver's runtime class without needing an instance
 * of `T`/`MT` up front; it also does not extend [SolverTest], since none of its assertions need a `solve` timeout.
 */
interface TestSolverConstruction<T : Solver, MT : MutableSolver> {
    companion object {
        inline fun <reified T : Solver, reified MT : MutableSolver> prototype(
            factory: SolverFactory,
            defaultBuiltins: Library,
        ): TestSolverConstruction<T, MT> = TestSolverConstructionImpl(factory, defaultBuiltins, T::class, MT::class)
    }

    /** Tests that [SolverFactory.solverOf], called with no argument, returns a [T] with every default property. */
    fun testCreatingEmptySolver()

    /**
     * Tests that [SolverFactory.solverOf], called with an explicit custom library runtime, flags, static/dynamic
     * theory and I/O channels, returns a [T] configured with exactly those values (and no default built-ins).
     */
    fun testCreatingCustomSolver()

    /** Tests that `SolverFactory.solverWithDefaultBuiltins()`, called with no argument, returns a [T] whose only
     * library is the suite's `defaultBuiltins`, with every other property left at its default. */
    fun testCreatingSolverWithDefaultBuiltins()

    /**
     * Tests that `SolverFactory.solverWithDefaultBuiltins`, called with an explicit custom library runtime, flags,
     * static/dynamic theory and I/O channels, returns a [T] whose libraries are the custom runtime *plus* the
     * suite's `defaultBuiltins`, with every other property matching the custom configuration.
     */
    fun testCreatingCustomSolverWithDefaultBuiltins()

    /** Same as [testCreatingEmptySolver], but for [SolverFactory.mutableSolverOf], expecting an [MT]. */
    fun testCreatingEmptyMutableSolver()

    /** Same as [testCreatingCustomSolver], but for `SolverFactory.mutableSolverOf`, expecting an [MT]. */
    fun testCreatingCustomMutableSolver()

    /** Same as [testCreatingSolverWithDefaultBuiltins], but for `SolverFactory.mutableSolverWithDefaultBuiltins`,
     * expecting an [MT]. */
    fun testCreatingMutableSolverWithDefaultBuiltins()

    /** Same as [testCreatingCustomSolverWithDefaultBuiltins], but for `SolverFactory.mutableSolverWithDefaultBuiltins`,
     * expecting an [MT]. */
    fun testCreatingCustomMutableSolverWithDefaultBuiltins()

    /**
     * Tests that [SolverFactory.newBuilder]`().noBuiltins().build()` returns a [T] with every default property
     * (equivalent to [testCreatingEmptySolver], but through the builder API).
     */
    fun testBuildingEmptySolver()

    /**
     * Tests that [SolverFactory.newBuilder]`().noBuiltins()`, configured with a custom runtime, flags,
     * static/dynamic theory and I/O channels, then `.build()`, returns a [T] matching that configuration
     * (equivalent to [testCreatingCustomSolver], but through the builder API).
     */
    fun testBuildingCustomSolver()

    /**
     * Tests that [SolverFactory.newBuilder]`().build()` (without calling `.noBuiltins()`) returns a [T] whose only
     * library is the suite's `defaultBuiltins` (equivalent to [testCreatingSolverWithDefaultBuiltins], but through
     * the builder API).
     */
    fun testBuildingSolverWithDefaultBuiltins()

    /**
     * Tests that [SolverFactory.newBuilder]`()`, configured with a custom runtime, flags, static/dynamic theory and
     * I/O channels, then `.build()` (without calling `.noBuiltins()`), returns a [T] whose libraries are the custom
     * runtime plus `defaultBuiltins` (equivalent to [testCreatingCustomSolverWithDefaultBuiltins], but through the
     * builder API).
     */
    fun testBuildingCustomSolverWithDefaultBuiltins()

    /** Same as [testBuildingEmptySolver], but calling `.buildMutable()`, expecting an [MT]. */
    fun testBuildingEmptyMutableSolver()

    /** Same as [testBuildingCustomSolver], but calling `.buildMutable()`, expecting an [MT]. */
    fun testBuildingCustomMutableSolver()

    /** Same as [testBuildingSolverWithDefaultBuiltins], but calling `.buildMutable()`, expecting an [MT]. */
    fun testBuildingMutableSolverWithDefaultBuiltins()

    /** Same as [testBuildingCustomSolverWithDefaultBuiltins], but calling `.buildMutable()`, expecting an [MT]. */
    fun testBuildingCustomMutableSolverWithDefaultBuiltins()
}
