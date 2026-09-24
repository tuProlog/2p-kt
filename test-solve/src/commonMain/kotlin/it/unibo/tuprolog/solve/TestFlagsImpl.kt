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

/** Concrete implementation of [TestFlags], instantiated via [TestFlags.prototype]. */
class TestFlagsImpl(
    private val solverFactory: SolverFactory,
) : TestFlags {
    override fun defaultLastCallOptimizationIsOn() {
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

    override fun defaultUnknownIsWarning() {
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

    override fun settingUnknownToAdmissibleValueSucceeds() {
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

    override fun flagsNamesMustBeAtoms() {
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

    override fun gettingMissingFlagsFails() {
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

    override fun settingMissingFlagsSucceeds() {
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

    override fun gettingFlagsByVariableEnumeratesFlags() {
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

    override fun settingFlagsByVariableGeneratesInstantiationError() {
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

    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
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

    override fun attemptingToEditMaxArityFlagProvokesPermissionError() {
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

    override fun defaultShowWildCardVariablesInSolutionsIsOn() {
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

    override fun defaultUniqueSolutionsIsOff() {
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

    override fun defaultGroundQueriesHaveBooleanSolutionIsOff() {
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

    override fun byDefaultWildcardVariablesAppearInSolutions() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val wildcard = varOf("_P")
            val query = (X eq "lino") and (wildcard eq "joey")

            val solution = solver.solveOnce(query, shortDuration)

            assertTrue(solution.isYes)
            assertEquals(Atom.of("lino"), solution.valueOf("X"))
            assertEquals(Atom.of("joey"), solution.valueOf("_P"))
        }
    }

    override fun hidingWildcardVariablesRemovesThemFromSolutions() {
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
            val query = (X eq "lino") and (wildcard eq "joey")

            val solution = solver.solveOnce(query, shortDuration)

            assertTrue(solution.isYes)
            assertEquals(Atom.of("lino"), solution.valueOf("X"))
            assertNull(solution.valueOf("_P"))
        }
    }

    override fun byDefaultDuplicateSolutionsAreAllReturned() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = member(X, logicListOf(1, 1, 1))

            assertEquals(3, solver.solveList(query, shortDuration).size)
        }
    }

    override fun uniqueSolutionsDropsSolutionsWithARepeatedTerm() {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.DEFAULT.set(UniqueSolutions, UniqueSolutions.ON),
                )
            val query = member(X, logicListOf(1, 1, 1))

            assertSolutionEquals(
                listOf(query.yes(X to 1)),
                solver.solveList(query, shortDuration),
            )
        }
    }
}
