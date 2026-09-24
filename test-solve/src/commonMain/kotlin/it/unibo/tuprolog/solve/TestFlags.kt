package it.unibo.tuprolog.solve

/**
 * Conformance tests for the `current_flag/2` and `set_flag/2` built-ins (`current_prolog_flag/2` and
 * `set_prolog_flag/2` are deprecated aliases kept for ISO naming compatibility), and for the
 * [it.unibo.tuprolog.solve.flags.FlagStore] they operate on: default values, admissible values, error cases
 * (non-atom names, unbound names/values, out-of-domain values), and the ability to both read and write flags that
 * are not part of the built-in set. Shared by every `Solver` implementation via the
 * `TestFlags.prototype(solverFactory)` factory (see `TestClassicFlags` in `:solve-classic` for a concrete usage).
 */
interface TestFlags : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFlags = TestFlagsImpl(solverFactory)
    }

    /**
     * Tests that [it.unibo.tuprolog.solve.flags.LastCallOptimization]'s default value is `on`, and that the query
     * ```prolog
     * ?- current_flag(last_call_optimization, on).
     * ```
     * succeeds on a solver initialized with default built-ins.
     */
    fun defaultLastCallOptimizationIsOn()

    /**
     * Tests that [it.unibo.tuprolog.solve.flags.Unknown]'s default value is `warning`, and that the query
     * ```prolog
     * ?- current_flag(unknown, warning).
     * ```
     * succeeds on a solver initialized with default built-ins.
     */
    fun defaultUnknownIsWarning()

    /**
     * Tests that, for every admissible value of the `unknown` flag, the query
     * ```prolog
     * ?- set_flag(unknown, Value), current_flag(unknown, V).
     * ```
     * succeeds binding `V` to `Value`.
     */
    fun settingUnknownToAdmissibleValueSucceeds()

    /**
     * Tests that every flag returned by `current_flag(F, _)` (with `F` unbound) is bound to an atom, and
     * that passing a non-atom flag name (an integer, a compound, a float) to either `current_flag/2` or
     * `set_flag/2` fails producing a `type_error(atom, Name)`.
     */
    fun flagsNamesMustBeAtoms()

    /**
     * Tests that, for flag names not present in a solver's default `flags` map (`a`, `b`, `c`), the query
     * ```prolog
     * ?- current_flag(a, V).
     * ```
     * simply fails (no solution), rather than raising an error.
     */
    fun gettingMissingFlagsFails()

    /**
     * Tests that `current_flag(F, X)`, with `F` unbound, enumerates on backtracking every flag currently set
     * on the solver (each name bound to `F`, each value bound to `X`), matching the solver's `flags` map exactly.
     */
    fun gettingFlagsByVariableEnumeratesFlags()

    /**
     * Tests that the query
     * ```prolog
     * ?- set_flag(F, value).
     * ```
     * with `F` unbound fails producing an `instantiation_error`.
     */
    fun settingFlagsByVariableGeneratesInstantiationError()

    /**
     * Tests that the query
     * ```prolog
     * ?- set_flag(last_call_optimization, true).
     * ```
     * (`true` not being an admissible value for `last_call_optimization`) fails producing a
     * `domain_error(flag_value(last_call_optimization), true)`.
     */
    fun settingWrongValueToLastCallOptimizationProvokesDomainError()

    /**
     * Tests that the query
     * ```prolog
     * ?- set_flag(max_arity, 10).
     * ```
     * fails producing `permission_error(modify, flag, max_arity)`, since `max_arity` is a read-only flag.
     */
    fun attemptingToEditMaxArityFlagProvokesPermissionError()

    /**
     * Tests that, for flag names not present in a solver's default `flags` map (`a`, `b`, `c`), the query
     * ```prolog
     * ?- set_flag(a, value), current_flag(a, X).
     * ```
     * succeeds binding `X` to `value`, and that the flag is subsequently present in the solver's `flags` map: unlike
     * the standard-defined flags, this solver implementation allows setting flags it did not previously know about.
     */
    fun settingMissingFlagsSucceeds()

    /**
     * Tests that [it.unibo.tuprolog.solve.flags.ShowWildCardVariablesInSolutions]'s default value is `on`, and
     * that the query
     * ```prolog
     * ?- current_flag(show_wildcard_variables_in_solutions, on).
     * ```
     * succeeds on a solver initialized with default built-ins.
     */
    fun defaultShowWildCardVariablesInSolutionsIsOn()

    /**
     * Tests that [it.unibo.tuprolog.solve.flags.UniqueSolutions]'s default value is `off`, and that the query
     * ```prolog
     * ?- current_flag(unique_solutions, off).
     * ```
     * succeeds on a solver initialized with default built-ins.
     */
    fun defaultUniqueSolutionsIsOff()

    /**
     * Tests that [it.unibo.tuprolog.solve.flags.GroundQueriesHaveBooleanSolution]'s default value is `off`, and
     * that the query
     * ```prolog
     * ?- current_flag(ground_queries_have_boolean_solution, off).
     * ```
     * succeeds on a solver initialized with default built-ins.
     */
    fun defaultGroundQueriesHaveBooleanSolutionIsOff()

    /**
     * Tests that, by default (`show_wildcard_variables_in_solutions` left `on`), a wildcard variable (e.g. `_P`)
     * still appears in a `yes` solution's substitution -- unaffected, standard behaviour (see #980).
     */
    fun byDefaultWildcardVariablesAppearInSolutions()

    /**
     * Tests that, with `show_wildcard_variables_in_solutions` set to `off`, a wildcard variable (e.g. `_P`) is
     * removed from a `yes` solution's substitution, while every other variable is kept -- fixing #980.
     */
    fun hidingWildcardVariablesRemovesThemFromSolutions()

    /**
     * Tests that, by default (`unique_solutions` left `off`), `member(X, [1, 1, 1])` yields three `yes` solutions
     * (one per list element), even though they all carry the same solved-query term -- unaffected, standard
     * behaviour.
     */
    fun byDefaultDuplicateSolutionsAreAllReturned()

    /**
     * Tests that, with `unique_solutions` set to `on`, `member(X, [1, 1, 1])` yields a single `yes` solution
     * (`X = 1`) instead of three, since the later two repeat the first one's solved-query term.
     */
    fun uniqueSolutionsDropsSolutionsWithARepeatedTerm()
}
