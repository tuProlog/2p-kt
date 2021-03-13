package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.MaxArity
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.utils.indexed
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestFlagsImpl(private val solverFactory: SolverFactory) : TestFlags {
    override fun defaultLastCallOptimizationIsOn() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(LastCallOptimization.ON, LastCallOptimization.defaultValue)

            val query = current_prolog_flag(LastCallOptimization.name, LastCallOptimization.ON)

            assertSolutionEquals(
                ktListOf(query.yes()),
                solver.solve(query, shortDuration).toList()
            )
        }
    }

    override fun defaultUnknownIsWarning() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertEquals(Unknown.WARNING, Unknown.defaultValue)

            val query = current_prolog_flag(Unknown.name, Unknown.WARNING)

            assertSolutionEquals(
                ktListOf(query.yes()),
                solver.solve(query, shortDuration).toList()
            )
        }
    }

    override fun settingUnknownToAdmissibleValueSucceeds() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            for (value in Unknown.admissibleValues) {
                val query = set_prolog_flag(Unknown.name, value) and current_prolog_flag(Unknown.name, V)

                assertSolutionEquals(
                    ktListOf(query.yes(V to value)),
                    solver.solve(query, shortDuration).toList()
                )
            }
        }
    }

    override fun flagsNamesMustBeAtoms() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val solutions = solver.solve(current_prolog_flag(F, `_`), shortDuration).toList()

            assertTrue { solutions.isNotEmpty() }

            for (sol in solutions.filterIsInstance<Solution.Yes>()) {
                assertTrue { sol.substitution[F] is Atom }
            }

            for (term in ktListOf(5, "f"("x"), 2.3).map { it.toTerm() }) {
                var query = current_prolog_flag(term, `_`)
                assertSolutionEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("current_prolog_flag", 2),
                            TypeError.Expected.ATOM,
                            term,
                            0
                        )
                    ),
                    solver.solveOnce(query, shortDuration)
                )

                query = set_prolog_flag(term, "value")
                assertSolutionEquals(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("set_prolog_flag", 2),
                            TypeError.Expected.ATOM,
                            term,
                            0
                        )
                    ),
                    solver.solveOnce(query, shortDuration)
                )
            }
        }
    }

    override fun gettingMissingFlagsFails() {
        prolog {
            for (flag in ktListOf("a", "b", "c")) {
                val solver = solverFactory.solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = current_prolog_flag(flag, V)

                assertSolutionEquals(
                    query.no(),
                    solver.solveOnce(query, shortDuration)
                )
            }
        }
    }

    override fun settingMissingFlagsSucceeds() {
        prolog {
            for ((value, flag) in ktListOf("a", "b", "c").asSequence().indexed()) {
                val solver = solverFactory.solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = set_prolog_flag(flag, value) and current_prolog_flag(flag, X)

                assertSolutionEquals(
                    query.yes(X to value),
                    solver.solveOnce(query, shortDuration)
                )

                assertEquals(value.toTerm(), solver.flags[flag])
            }
        }
    }

    override fun gettingFlagsByVariableEnumeratesFlags() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val defaultFlags = solver.flags.mapKeys { (k, _) -> k.toTerm() }

            assertTrue { defaultFlags.isNotEmpty() }

            val query = current_prolog_flag(F, X)

            val selectedFlags = solver.solve(query, shortDuration)
                .filterIsInstance<Solution.Yes>()
                .map { it.substitution[F]!! to it.substitution[X]!! }
                .toMap()

            assertEquals(defaultFlags, selectedFlags)
        }
    }

    override fun settingFlagsByVariableGeneratesInstantiationError() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_prolog_flag(F, "value")

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("set_prolog_flag", 2),
                            F,
                            index = 0
                        )
                    )
                ),
                solver.solveList(query, shortDuration)
            )
        }
    }

    override fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_prolog_flag(LastCallOptimization.name, truthOf(true))

            assertFalse { LastCallOptimization.admissibleValues.contains(truthOf(true)) }

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        DomainError.forFlagValues(
                            DummyInstances.executionContext,
                            Signature("set_prolog_flag", 2),
                            LastCallOptimization.admissibleValues.asIterable(),
                            truthOf(true),
                            index = 1
                        )
                    )
                ),
                solver.solveList(query, shortDuration)
            )
        }
    }

    override fun attemptingToEditMaxArityFlagProvokesPermissionError() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = set_prolog_flag(MaxArity.name, 10)

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("set_prolog_flag", 2),
                            PermissionError.Operation.MODIFY,
                            PermissionError.Permission.FLAG,
                            atomOf(MaxArity.name)
                        )
                    )
                ),
                solver.solveList(query, shortDuration)
            )
        }
    }
}
