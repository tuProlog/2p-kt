package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals

class TestStackTraceImpl(private val solverFactory: SolverFactory) : TestStackTrace {

    private fun threeLayersTheory(errorExpression: PrologScope.() -> Struct): Theory =
        prolog {
            theoryOf(
                rule { "foo"(X) impliedBy "bar"(X) },
                rule { "bar"(X) impliedBy "baz"(X) },
                rule { "baz"(X) impliedBy errorExpression() }
            )
        }

    override fun testSimpleStackTrace() {
        prolog {
            val Y = Y
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = "foo"(X)
            val solution = solver.solveOnce(query)
            assertSolutionEquals(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("+", 2),
                        Y,
                        index = 0
                    )
                ),
                solution
            )

            assertEquals(
                ktListOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "?-" / 1),
                (solution as Solution.Halt).exception.prologStackTrace.map { it.indicator }
            )
        }
    }

    override fun testDoubleStackTrace() {
        prolog {
            val Y = Y
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = findall(X, "foo"(X), L)
            val solution = solver.solveOnce(query)
            assertSolutionEquals(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("+", 2),
                        Y,
                        index = 0
                    )
                ),
                solution
            )

            assertEquals(
                ktListOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.prologStackTrace.map { it.indicator }
            )
        }
    }

    override fun testTripleStackTrace() {
        prolog {
            val Y = Y
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = threeLayersTheory { X `is` (Y + 1) })

            val query = findall(X, bagof(Z, "foo"(Z), X), L)
            val solution = solver.solveOnce(query)
            assertSolutionEquals(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("+", 2),
                        Y,
                        index = 0
                    )
                ),
                solution
            )

            assertEquals(
                ktListOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.prologStackTrace.map { it.indicator }
            )
        }
    }

    override fun testThrowIsNotInStacktrace() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = threeLayersTheory { `throw`("x") })

            val query = findall(X, bagof(Z, "foo"(Z), X), L)
            val solution = solver.solveOnce(query)
            assertSolutionEquals(
                query.halt(
                    SystemError.forUncaughtError(
                        MessageError(
                            context = DummyInstances.executionContext,
                            extraData = atomOf("x")
                        )
                    )
                ),
                solution
            )

            assertEquals(
                ktListOf("baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.prologStackTrace.map { it.indicator }
            )
        }
    }
}
