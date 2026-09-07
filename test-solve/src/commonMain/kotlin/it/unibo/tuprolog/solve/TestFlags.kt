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
}
