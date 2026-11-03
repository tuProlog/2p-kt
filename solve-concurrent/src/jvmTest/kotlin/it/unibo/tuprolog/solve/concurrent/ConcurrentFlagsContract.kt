@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
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
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.utils.indexed

fun concurrentFlagsContract() =
    concurrentContract(
        disabled =
            setOf(
                "flags/setting-missing-flags-succeeds",
            ),
    ) {
        scenario("flags/default-last-call-optimization-is-on") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                LastCallOptimization.defaultValue shouldBe LastCallOptimization.ON

                val query = current_flag(LastCallOptimization.name, LastCallOptimization.ON)
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/default-unknown-is-warning") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                Unknown.defaultValue shouldBe Unknown.WARNING

                val query = current_flag(Unknown.name, Unknown.WARNING)
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/setting-unknown-to-admissible-value-succeeds") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                for (value in Unknown.admissibleValues) {
                    val query = set_flag(Unknown.name, value) and current_flag(Unknown.name, V)
                    val solutions = fromSequence(solver.solve(query, shortBudget))
                    val expected = fromSequence(query.yes(V to value))

                    expected.assertingEquals(solutions)
                }
            }
        }

        scenario("flags/flags-names-must-be-atoms") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val solutions = solver.solve(current_flag(F, `_`), shortBudget).toList()

                solutions.isNotEmpty().shouldBeTrue()

                for (sol in solutions.filterIsInstance<Solution.Yes>()) {
                    sol.substitution[F].shouldBeInstanceOf<Atom>()
                }

                for (term in listOf(5, "f"("x"), 2.3).map { it.toTerm() }) {
                    var query = current_flag(term, `_`)
                    var solutions2 = fromSequence(solver.solveOnce(query, shortBudget))
                    var expected =
                        fromSequence(
                            query.halt(
                                TypeError.forArgument(
                                    DummyInstances.executionContext,
                                    CurrentFlag.signature,
                                    TypeError.Expected.ATOM,
                                    term,
                                    0,
                                ),
                            ),
                        )

                    expected.assertingEquals(solutions2)

                    query = set_flag(term, "value")
                    solutions2 = fromSequence(solver.solveOnce(query, shortBudget))
                    expected =
                        fromSequence(
                            query.halt(
                                TypeError.forArgument(
                                    DummyInstances.executionContext,
                                    SetFlag.signature,
                                    TypeError.Expected.ATOM,
                                    term,
                                    0,
                                ),
                            ),
                        )

                    expected.assertingEquals(solutions2)
                }
            }
        }

        scenario("flags/getting-missing-flags-fails") {
            logicProgramming {
                for (flag in listOf("a", "b", "c")) {
                    val solver = solverWithDefaultBuiltins()

                    solver.flags.containsKey(flag).shouldBeFalse()

                    val query = current_flag(flag, V)
                    val solutions = fromSequence(solver.solveOnce(query, shortBudget))
                    val expected = fromSequence(query.no())

                    expected.assertingEquals(solutions)
                }
            }
        }

        // todo handle set flag side effect
        scenario("flags/setting-missing-flags-succeeds") {
            logicProgramming {
                for ((value, flag) in listOf("a", "b", "c").asSequence().indexed()) {
                    val solver = solverWithDefaultBuiltins()

                    solver.flags.containsKey(flag).shouldBeFalse()

                    val query = set_flag(flag, value) and current_flag(flag, X)
                    val solutions = fromSequence(solver.solveOnce(query, shortBudget))
                    val expected = fromSequence(query.yes(X to value))

                    expected.assertingEquals(solutions)

                    solver.flags[flag] shouldBe value.toTerm()
                }
            }
        }

        scenario("flags/getting-flags-by-variable-enumerates-flags") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val defaultFlags = solver.flags.mapKeys { (k, _) -> k.toTerm() }

                defaultFlags.isNotEmpty().shouldBeTrue()

                val query = current_flag(F, X)

                val selectedFlags =
                    solver
                        .solve(query, shortBudget)
                        .filterIsInstance<Solution.Yes>()
                        .map { it.substitution[F]!! to it.substitution[X]!! }
                        .toMap()

                selectedFlags shouldBe defaultFlags
            }
        }

        scenario("flags/setting-flags-by-variable-generates-instantiation-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = set_flag(F, "value")
                val solutions = fromSequence(solver.solveList(query, shortBudget))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                SetFlag.signature,
                                F,
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/setting-wrong-value-to-last-call-optimization-provokes-domain-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = set_flag(LastCallOptimization.name, truthOf(true))

                LastCallOptimization.admissibleValues.contains(truthOf(true)).shouldBeFalse()

                val solutions = fromSequence(solver.solveList(query, shortBudget))
                val expected =
                    fromSequence(
                        query.halt(
                            DomainError.forFlagValues(
                                DummyInstances.executionContext,
                                SetFlag.signature,
                                LastCallOptimization.admissibleValues.asIterable(),
                                truthOf(true),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/attempting-to-edit-max-arity-flag-provokes-permission-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = set_flag(MaxArity.name, 10)
                val solutions = fromSequence(solver.solveList(query, shortBudget))
                val expected =
                    fromSequence(
                        query.halt(
                            PermissionError.of(
                                DummyInstances.executionContext,
                                SetFlag.signature,
                                PermissionError.Operation.MODIFY,
                                PermissionError.Permission.FLAG,
                                atomOf(MaxArity.name),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/default-show-wild-card-variables-in-solutions-is-on") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                ShowWildCardVariablesInSolutions.defaultValue shouldBe ShowWildCardVariablesInSolutions.ON

                val query = current_flag(ShowWildCardVariablesInSolutions.name, ShowWildCardVariablesInSolutions.ON)
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/default-unique-solutions-is-off") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                UniqueSolutions.defaultValue shouldBe UniqueSolutions.OFF

                val query = current_flag(UniqueSolutions.name, UniqueSolutions.OFF)
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/default-ground-queries-have-boolean-solution-is-off") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                GroundQueriesHaveBooleanSolution.defaultValue shouldBe GroundQueriesHaveBooleanSolution.OFF

                val query = current_flag(GroundQueriesHaveBooleanSolution.name, GroundQueriesHaveBooleanSolution.OFF)
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("flags/by-default-wildcard-variables-appear-in-solutions") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val wildcard = varOf("_P")
                val query = structOf("f", X, wildcard) eq structOf("f", "lino", "joey")

                val solution = solver.solveOnce(query, shortBudget)

                solution.isYes.shouldBeTrue()
                solution.valueOf("X") shouldBe Atom.of("lino")
                solution.valueOf("_P") shouldBe Atom.of("joey")
            }
        }

        scenario("flags/hiding-wildcard-variables-removes-them-from-solutions") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        flags =
                            FlagStore.DEFAULT.set(
                                ShowWildCardVariablesInSolutions,
                                ShowWildCardVariablesInSolutions.OFF,
                            ),
                    )
                val wildcard = varOf("_P")
                val query = structOf("f", X, wildcard) eq structOf("f", "lino", "joey")

                val solution = solver.solveOnce(query, shortBudget)

                solution.isYes.shouldBeTrue()
                solution.valueOf("X") shouldBe Atom.of("lino")
                solution.valueOf("_P").shouldBeNull()
            }
        }

        scenario("flags/by-default-duplicate-solutions-are-all-returned") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val query = member(X, logicListOf(1, 1, 1))

                (solver.solveList(query, shortBudget).count { it.isYes }) shouldBe 3
            }
        }

        scenario("flags/unique-solutions-drops-solutions-with-a-repeated-term") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.DEFAULT.set(UniqueSolutions, UniqueSolutions.ON),
                    )
                val query = member(X, logicListOf(1, 1, 1))
                val solutions = fromSequence(solver.solve(query, shortBudget))
                val expected = fromSequence(query.yes(X to 1))

                expected.assertingEquals(solutions)
            }
        }
    }
