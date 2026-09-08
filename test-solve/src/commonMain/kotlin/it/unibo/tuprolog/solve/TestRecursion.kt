package it.unibo.tuprolog.solve

/**
 * Conformance tests exercising recursive rules, both to check that recursion (with backtracking-driven mutable
 * state, and deep tail recursion) actually resolves correctly, and that
 * [it.unibo.tuprolog.solve.flags.LastCallOptimization] measurably affects how deep the logic stack trace grows on
 * a failing recursive computation. Shared by every `Solver` implementation via the
 * `TestRecursion.prototype(solverFactory)` factory (see `TestClassicRecursion` in `:solve-classic` for a concrete
 * usage).
 */
interface TestRecursion : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestRecursion = TestRecursionImpl(solverFactory)
    }

    /**
     * Tests a hand-written thermostat-like theory (a mutable `temp/1` fact adjusted one degree at a time via
     * `retract/1`+`assert/1`, driven by a recursive `check_temperature/0` rule) starting at `10` and converging,
     * printing each intermediate temperature, up to the low end (`18`) of the target range `18..22`.
     */
    fun testRecursion1()

    /** Same theory as [testRecursion1], but starting at `30`, converging down to the high end (`22`) of the range. */
    fun testRecursion2()

    /**
     * Tests that, with [it.unibo.tuprolog.solve.flags.LastCallOptimization] `on`, a 100-deep tail-recursive `recursive/1`
     * predicate that eventually throws leaves only 2 frames (the throwing goal and the top-level query) in the
     * resulting [Solution.Halt]'s logic stack trace, since tail calls do not accumulate stack frames.
     */
    fun testTailRecursion()

    /**
     * Same recursive theory as [testTailRecursion], but with [it.unibo.tuprolog.solve.flags.LastCallOptimization]
     * `off`: the logic stack trace grows by one frame per recursive call, ending up `n + 2` frames deep.
     */
    fun testNonTailRecursion()
}
