package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.GroundQueriesHaveBooleanSolution
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.MaxArity
import it.unibo.tuprolog.solve.flags.ShowWildCardVariablesInSolutions
import it.unibo.tuprolog.solve.flags.UniqueSolutions
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.utils.indexed
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Conformance tests for the `current_flag/2` and `set_flag/2` built-ins (`current_prolog_flag/2` and `set_prolog_flag/2` are deprecated aliases kept for ISO naming compatibility), and for the [it.unibo.tuprolog.solve.flags.FlagStore] they operate on: default values, admissible values, error cases (non-atom names, unbound names/values, out-of-domain values), and the ability to both read and write flags that are not part of the built-in set. */
fun flagsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests that [it.unibo.tuprolog.solve.flags.LastCallOptimization]'s default value is `on`, and that the query
    // ```prolog
    // ?- current_flag(last_call_optimization, on).
    // ```
    // succeeds on a solver initialized with default built-ins.
    scenario("flags/default-last-call-optimization-is-on") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(LastCallOptimization.ON, LastCallOptimization.defaultValue)

            val query = current_flag(LastCallOptimization.name, LastCallOptimization.ON)

            assertSolutionEquals(
                listOf(query.yes()),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that [it.unibo.tuprolog.solve.flags.Unknown]'s default value is `warning`, and that the query
    // ```prolog
    // ?- current_flag(unknown, warning).
    // ```
    // succeeds on a solver initialized with default built-ins.
    scenario("flags/default-unknown-is-warning") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(Unknown.WARNING, Unknown.defaultValue)

            val query = current_flag(Unknown.name, Unknown.WARNING)

            assertSolutionEquals(
                listOf(query.yes()),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that, for every admissible value of the `unknown` flag, the query
    // ```prolog
    // ?- set_flag(unknown, Value), current_flag(unknown, V).
    // ```
    // succeeds binding `V` to `Value`.
    scenario("flags/setting-unknown-to-admissible-value-succeeds") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            for (value in Unknown.admissibleValues) {
                val query = set_flag(Unknown.name, value) and current_flag(Unknown.name, V)

                assertSolutionEquals(
                    listOf(query.yes(V to value)),
                    solver.solve(query, shortDuration).toList(),
                )
            }
        }
    }

    // Tests that every flag returned by `current_flag(F, _)` (with `F` unbound) is bound to an atom, and
    // that passing a non-atom flag name (an integer, a compound, a float) to either `current_flag/2` or
    // `set_flag/2` fails producing a `type_error(atom, Name)`.
    scenario("flags/flags-names-must-be-atoms") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val solutions = solver.solve(current_flag(F, `_`), shortDuration).toList()

            assertTrue { solutions.isNotEmpty() }

            for (sol in solutions.filterIsInstance<Solution.Yes>()) {
                assertTrue { sol.substitution[F] is Atom }
            }

            for (term in listOf(5, "f"("x"), 2.3).map { it.toTerm() }) {
                var query = current_flag(term, `_`)
                assertSolutionEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            CurrentFlag.signature,
                            TypeError.Expected.ATOM,
                            term,
                            0,
                        ),
                    ),
                    solver.solveOnce(query, shortDuration),
                )

                query = set_flag(term, "value")
                assertSolutionEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            SetFlag.signature,
                            TypeError.Expected.ATOM,
                            term,
                            0,
                        ),
                    ),
                    solver.solveOnce(query, shortDuration),
                )
            }
        }
    }

    // Tests that, for flag names not present in a solver's default `flags` map (`a`, `b`, `c`), the query
    // ```prolog
    // ?- current_flag(a, V).
    // ```
    // simply fails (no solution), rather than raising an error.
    scenario("flags/getting-missing-flags-fails") {
        logicProgramming {
            for (flag in listOf("a", "b", "c")) {
                val solver = solverFactory.solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = current_flag(flag, V)

                assertSolutionEquals(
                    query.no(),
                    solver.solveOnce(query, shortDuration),
                )
            }
        }
    }

    // Tests that `current_flag(F, X)`, with `F` unbound, enumerates on backtracking every flag currently set
    // on the solver (each name bound to `F`, each value bound to `X`), matching the solver's `flags` map exactly.
    scenario("flags/getting-flags-by-variable-enumerates-flags") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val defaultFlags = solver.flags.mapKeys { (k, _) -> k.toTerm() }

            assertTrue { defaultFlags.isNotEmpty() }

            val query = current_flag(F, X)

            val selectedFlags =
                solver
                    .solve(query, shortDuration)
                    .filterIsInstance<Solution.Yes>()
                    .map { it.substitution[F]!! to it.substitution[X]!! }
                    .toMap()

            assertEquals(defaultFlags, selectedFlags)
        }
    }

    // Tests that the query
    // ```prolog
    // ?- set_flag(F, value).
    // ```
    // with `F` unbound fails producing an `instantiation_error`.
    scenario("flags/setting-flags-by-variable-generates-instantiation-error") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_flag(F, "value")

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            SetFlag.signature,
                            F,
                            index = 0,
                        ),
                    ),
                ),
                solver.solveList(query, shortDuration),
            )
        }
    }

    // Tests that the query
    // ```prolog
    // ?- set_flag(last_call_optimization, true).
    // ```
    // (`true` not being an admissible value for `last_call_optimization`) fails producing a
    // `domain_error(flag_value(last_call_optimization), true)`.
    scenario("flags/setting-wrong-value-to-last-call-optimization-provokes-domain-error") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_flag(LastCallOptimization.name, truthOf(true))

            assertFalse { LastCallOptimization.admissibleValues.contains(truthOf(true)) }

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forFlagValues(
                            DummyInstances.executionContext,
                            SetFlag.signature,
                            LastCallOptimization.admissibleValues.asIterable(),
                            truthOf(true),
                            index = 1,
                        ),
                    ),
                ),
                solver.solveList(query, shortDuration),
            )
        }
    }

    // Tests that the query
    // ```prolog
    // ?- set_flag(max_arity, 10).
    // ```
    // fails producing `permission_error(modify, flag, max_arity)`, since `max_arity` is a read-only flag.
    scenario("flags/attempting-to-edit-max-arity-flag-provokes-permission-error") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_flag(MaxArity.name, 10)

            assertSolutionEquals(
                listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            SetFlag.signature,
                            PermissionError.Operation.MODIFY,
                            PermissionError.Permission.FLAG,
                            atomOf(MaxArity.name),
                        ),
                    ),
                ),
                solver.solveList(query, shortDuration),
            )
        }
    }

    // Tests that, for flag names not present in a solver's default `flags` map (`a`, `b`, `c`), the query
    // ```prolog
    // ?- set_flag(a, value), current_flag(a, X).
    // ```
    // succeeds binding `X` to `value`, and that the flag is subsequently present in the solver's `flags` map: unlike
    // the standard-defined flags, this solver implementation allows setting flags it did not previously know about.
    scenario("flags/setting-missing-flags-succeeds") {
        logicProgramming {
            for ((value, flag) in listOf("a", "b", "c").asSequence().indexed()) {
                val solver = solverFactory.solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = set_flag(flag, value) and current_flag(flag, X)

                assertSolutionEquals(
                    query.yes(X to value),
                    solver.solveOnce(query, shortDuration),
                )

                assertEquals(value.toTerm(), solver.flags[flag])
            }
        }
    }

    // Tests that [it.unibo.tuprolog.solve.flags.ShowWildCardVariablesInSolutions]'s default value is `on`, and
    // that the query
    // ```prolog
    // ?- current_flag(show_wildcard_variables_in_solutions, on).
    // ```
    // succeeds on a solver initialized with default built-ins.
    scenario("flags/default-show-wild-card-variables-in-solutions-is-on") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(ShowWildCardVariablesInSolutions.ON, ShowWildCardVariablesInSolutions.defaultValue)

            val query = current_flag(ShowWildCardVariablesInSolutions.name, ShowWildCardVariablesInSolutions.ON)

            assertSolutionEquals(
                listOf(query.yes()),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that [it.unibo.tuprolog.solve.flags.UniqueSolutions]'s default value is `off`, and that the query
    // ```prolog
    // ?- current_flag(unique_solutions, off).
    // ```
    // succeeds on a solver initialized with default built-ins.
    scenario("flags/default-unique-solutions-is-off") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(UniqueSolutions.OFF, UniqueSolutions.defaultValue)

            val query = current_flag(UniqueSolutions.name, UniqueSolutions.OFF)

            assertSolutionEquals(
                listOf(query.yes()),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that [it.unibo.tuprolog.solve.flags.GroundQueriesHaveBooleanSolution]'s default value is `off`, and
    // that the query
    // ```prolog
    // ?- current_flag(ground_queries_have_boolean_solution, off).
    // ```
    // succeeds on a solver initialized with default built-ins.
    scenario("flags/default-ground-queries-have-boolean-solution-is-off") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(GroundQueriesHaveBooleanSolution.OFF, GroundQueriesHaveBooleanSolution.defaultValue)

            val query = current_flag(GroundQueriesHaveBooleanSolution.name, GroundQueriesHaveBooleanSolution.OFF)

            assertSolutionEquals(
                listOf(query.yes()),
                solver.solve(query, shortDuration).toList(),
            )
        }
    }

    // Tests that, by default (`show_wildcard_variables_in_solutions` left `on`), a wildcard variable (e.g. `_P`)
    // still appears in a `yes` solution's substitution -- unaffected, standard behaviour (see #980).
    scenario("flags/by-default-wildcard-variables-appear-in-solutions") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val wildcard = varOf("_P")
            val query = structOf("f", X, wildcard) eq structOf("f", "lino", "joey")

            val solution = solver.solveOnce(query, shortDuration)

            assertTrue(solution.isYes)
            assertEquals(Atom.of("lino"), solution.valueOf("X"))
            assertEquals(Atom.of("joey"), solution.valueOf("_P"))
        }
    }

    // Tests that, with `show_wildcard_variables_in_solutions` set to `off`, a wildcard variable (e.g. `_P`) is
    // removed from a `yes` solution's substitution, while every other variable is kept -- fixing #980.
    scenario("flags/hiding-wildcard-variables-removes-them-from-solutions") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags =
                        FlagStore.DEFAULT.set(
                            ShowWildCardVariablesInSolutions,
                            ShowWildCardVariablesInSolutions.OFF,
                        ),
                )
            val wildcard = varOf("_P")
            val query = structOf("f", X, wildcard) eq structOf("f", "lino", "joey")

            val solution = solver.solveOnce(query, shortDuration)

            assertTrue(solution.isYes)
            assertEquals(Atom.of("lino"), solution.valueOf("X"))
            assertNull(solution.valueOf("_P"))
        }
    }

    // Tests that, by default (`unique_solutions` left `off`), `member(X, [1, 1, 1])` yields three `yes` solutions
    // (one per list element), even though they all carry the same solved-query term -- unaffected, standard
    // behaviour.
    scenario("flags/by-default-duplicate-solutions-are-all-returned") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = member(X, logicListOf(1, 1, 1))

            assertEquals(3, solver.solveList(query, shortDuration).count { it.isYes })
        }
    }

    // Tests that, with `unique_solutions` set to `on`, `member(X, [1, 1, 1])` yields a single `yes` solution
    // (`X = 1`) instead of three, since the later two repeat the first one's solved-query term.
    scenario("flags/unique-solutions-drops-solutions-with-a-repeated-term") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.DEFAULT.set(UniqueSolutions, UniqueSolutions.ON),
                )
            val query = member(X, logicListOf(1, 1, 1))

            assertEquals(1, solver.solveList(query, shortDuration).count { it.isYes })
        }
    }
}
