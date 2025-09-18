package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals

@Suppress("LocalVariableName", "ktlint:standard:property-naming", "VariableNaming")
interface TestConcurrentStackTrace<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    private fun threeLayersTheory(errorExpression: LogicProgrammingScope.() -> Struct): Theory =
        logicProgramming {
            theoryOf(
                rule { "foo"(X) impliedBy "bar"(X) },
                rule { "bar"(X) impliedBy "baz"(X) },
                rule { "baz"(X) impliedBy errorExpression() },
            )
        }

    fun testSimpleStackTrace() {
        logicProgramming {
            val Y = Y
            val solver = solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = "foo"(X)
            val solution = solver.solveOnce(query)
            val solutions = fromSequence(solution)
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("+", 2),
                            Y,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    fun testDoubleStackTrace() {
        logicProgramming {
            val Y = Y
            val solver = solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = findall(X, "foo"(X), L)
            val solution = solver.solveOnce(query)
            val solutions = fromSequence(solution)
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("+", 2),
                            Y,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    fun testTripleStackTrace() {
        logicProgramming {
            val Y = Y
            val solver = solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = findall(X, bagof(Z, "foo"(Z), X), L)
            val solution = solver.solveOnce(query)
            val solutions = fromSequence(solution)
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("+", 2),
                            Y,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    fun testThrowIsNotInStacktrace() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins(staticKb = threeLayersTheory { `throw`("x") })

            val query = findall(X, bagof(Z, "foo"(Z), X), L)
            val solution = solver.solveOnce(query)
            val solutions = fromSequence(solution)
            val expected =
                fromSequence(
                    query.halt(
                        SystemError.forUncaughtError(
                            MessageError(
                                context = DummyInstances.executionContext,
                                extraData = atomOf("x"),
                            ),
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            assertEquals(
                listOf("baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }
}
