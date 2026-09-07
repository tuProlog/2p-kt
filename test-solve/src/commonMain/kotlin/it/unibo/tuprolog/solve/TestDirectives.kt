package it.unibo.tuprolog.solve

/**
 * Conformance tests for directive clauses (`:- Goal.`), covering `dynamic/1`, `static/1`, `initialization/1`,
 * `solve/1`, `set_flag/2`, `set_prolog_flag/2`, `op/3`, malformed directives, and directives that fail or raise
 * during theory loading. Shared by every `Solver` implementation via the `TestDirectives.prototype(solverFactory)`
 * factory (see `TestClassicDirectives` in `:solve-classic` for a concrete usage).
 *
 * Every test case that loads a directive-bearing theory does so via [DirectiveTestsUtils.solverInitializers] or
 * [DirectiveTestsUtils.solverInitializersWithEventsList], i.e. through all four equivalent ways a theory can end up
 * as a solver's knowledge base (as the static/dynamic theory passed to a factory method, or loaded afterwards into
 * a fresh `MutableSolver`), since a directive must be honored the same way regardless of how its theory was loaded.
 */
interface TestDirectives : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestDirectives = TestDirectivesImpl(solverFactory)
    }

    /**
     * Tests that a theory containing
     * ```prolog
     * f(1). f(2). f(3).
     * :- static(g/1).
     * g(4). g(5). g(6).
     * :- dynamic(h/1).
     * h(7). h(8). h(9).
     * ```
     * loaded as a solver's static knowledge base ends up with `f/1` and `g/1`'s facts (the ones before the first
     * `dynamic/1` directive) in the static theory, and `h/1`'s facts in the dynamic theory; and that loading the
     * same theory into a fresh `MutableSolver` via `loadStaticKb` produces the same split.
     */
    fun testDynamic1()

    /**
     * Tests that a theory with the same shape as in [testDynamic1] but loaded as a solver's *dynamic* knowledge
     * base ends up with `g/1`'s facts (following the `static/1` directive) moved to the static theory, and every
     * other fact (`f/1` and `h/1`) remaining in the dynamic theory; and that loading it into a fresh `MutableSolver`
     * via `loadDynamicKb` produces the same split.
     */
    fun testStatic1()

    /**
     * Tests that, for a theory containing an `initialization/1` directive whose goal writes to the standard output
     * and calls another predicate that also writes, loading the theory (via any of
     * [DirectiveTestsUtils.solverInitializers]) triggers those writes, in clause order, exactly once each, and that
     * the predicate defined after the last `initialization/1` directive remains solvable afterwards.
     */
    fun testInitialization1()

    /** Same as [testInitialization1], but for the `solve/1` directive instead of `initialization/1`. */
    fun testSolve1()

    /**
     * Tests that a theory mixing malformed directives (`op("a", xfx, "++")` with a non-integer priority,
     * `op(3, "b", "+++")` with a non-atom specifier, `set_flag(a, x)` with an inadmissible value, and misspelled
     * `dinamic/1`/`statyc/1` directives) with well-formed facts still loads without emitting any writing/warning
     * event, and that the facts end up in the theory (static or dynamic, depending on how it was loaded).
     */
    fun testWrongDirectives()

    /**
     * Tests that a theory containing
     * ```prolog
     * :- set_flag(a, 1).
     * :- set_flag(b, 2).
     * :- set_flag(c, 3).
     * ```
     * loaded via any of [DirectiveTestsUtils.solverInitializers] results in a solver whose `flags` map contains `a`,
     * `b` and `c` bound to `1`, `2` and `3` respectively, alongside the default flags.
     */
    fun testSetFlag2()

    /** Same as [testSetFlag2], but using the `set_prolog_flag/2` directive instead of `set_flag/2`. */
    fun testSetPrologFlag2()

    /**
     * Tests that a theory containing
     * ```prolog
     * :- op(2, xfx, '++').
     * :- op(3, xfy, '+++').
     * :- op(4, yfx, '++++').
     * ```
     * loaded via any of [DirectiveTestsUtils.solverInitializers] results in a solver whose `operators` set contains
     * all three newly-declared operators, with the declared priority and specifier.
     */
    fun testOp3()

    /**
     * Tests that a theory with a single `initialization/1` directive whose goal always fails still loads (the fact
     * ends up in the appropriate theory), and that exactly one `InitializationIssue` warning event, mentioning
     * "failure", is emitted.
     */
    fun testFailingInitialization1()

    /** Same as [testFailingInitialization1], but for the `solve/1` directive instead of `initialization/1`. */
    fun testFailingSolve1()

    /**
     * Tests that a theory with a single `initialization/1` directive whose goal raises an [it.unibo.tuprolog.solve.exception.error.InstantiationError]
     * (an unbound variable used in an arithmetic expression) still loads, and that exactly one `InitializationIssue`
     * warning event, mentioning the raised error, is emitted.
     */
    fun testExceptionalInitialization1()

    /** Same as [testExceptionalInitialization1], but for the `solve/1` directive instead of `initialization/1`. */
    fun testExceptionalSolve1()

    /**
     * Tests that loading, with default built-ins, a theory of [DirectiveTestsUtils.bigTheory]'s default size
     * (40000 facts) completes at all within this suite's test timeout — a basic guard against quadratic-or-worse
     * loading time regressions. Unlike the other test cases here, it does not inspect the resulting solver.
     */
    fun testDirectiveLoadingQuickly()
}
