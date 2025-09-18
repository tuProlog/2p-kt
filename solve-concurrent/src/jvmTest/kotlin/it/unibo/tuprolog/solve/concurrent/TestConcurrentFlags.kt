package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.MaxArity
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.utils.indexed
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface TestConcurrentFlags<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun defaultLastCallOptimizationIsOn() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            assertEquals(LastCallOptimization.ON, LastCallOptimization.defaultValue)

            val query = current_flag(LastCallOptimization.name, LastCallOptimization.ON)
            val solutions = fromSequence(solver.solve(query, shortDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun defaultUnknownIsWarning() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            assertEquals(Unknown.WARNING, Unknown.defaultValue)

            val query = current_flag(Unknown.name, Unknown.WARNING)
            val solutions = fromSequence(solver.solve(query, shortDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun settingUnknownToAdmissibleValueSucceeds() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            for (value in Unknown.admissibleValues) {
                val query = set_flag(Unknown.name, value) and current_flag(Unknown.name, V)
                val solutions = fromSequence(solver.solve(query, shortDuration))
                val expected = fromSequence(query.yes(V to value))

                expected.assertingEquals(solutions)
            }
        }
    }

    fun flagsNamesMustBeAtoms() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val solutions = solver.solve(current_flag(F, `_`), shortDuration).toList()

            assertTrue { solutions.isNotEmpty() }

            for (sol in solutions.filterIsInstance<Solution.Yes>()) {
                assertTrue { sol.substitution[F] is Atom }
            }

            for (term in listOf(5, "f"("x"), 2.3).map { it.toTerm() }) {
                var query = current_flag(term, `_`)
                var solutions2 = fromSequence(solver.solveOnce(query, shortDuration))
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
                solutions2 = fromSequence(solver.solveOnce(query, shortDuration))
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

    fun gettingMissingFlagsFails() {
        logicProgramming {
            for (flag in listOf("a", "b", "c")) {
                val solver = solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = current_flag(flag, V)
                val solutions = fromSequence(solver.solveOnce(query, shortDuration))
                val expected = fromSequence(query.no())

                expected.assertingEquals(solutions)
            }
        }
    }

    fun settingMissingFlagsSucceeds() {
        logicProgramming {
            for ((value, flag) in listOf("a", "b", "c").asSequence().indexed()) {
                val solver = solverWithDefaultBuiltins()

                assertFalse { solver.flags.containsKey(flag) }

                val query = set_flag(flag, value) and current_flag(flag, X)
                val solutions = fromSequence(solver.solveOnce(query, shortDuration))
                val expected = fromSequence(query.yes(X to value))

                expected.assertingEquals(solutions)

                assertEquals(value.toTerm(), solver.flags[flag])
            }
        }
    }

    fun gettingFlagsByVariableEnumeratesFlags() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()
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

    fun settingFlagsByVariableGeneratesInstantiationError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = set_flag(F, "value")
            val solutions = fromSequence(solver.solveList(query, shortDuration))
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

    fun settingWrongValueToLastCallOptimizationProvokesDomainError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = set_flag(LastCallOptimization.name, truthOf(true))

            assertFalse { LastCallOptimization.admissibleValues.contains(truthOf(true)) }

            val solutions = fromSequence(solver.solveList(query, shortDuration))
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

    fun attemptingToEditMaxArityFlagProvokesPermissionError() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = set_flag(MaxArity.name, 10)
            val solutions = fromSequence(solver.solveList(query, shortDuration))
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
}
