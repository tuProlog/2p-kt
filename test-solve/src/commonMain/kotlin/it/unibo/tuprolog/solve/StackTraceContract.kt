@file:Suppress("LocalVariableName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals

/** Conformance tests for a resolution exception's `logicStackTrace`: the sequence of goal [Signature]s active when the exception was raised, from the innermost failing goal to the top-level query. */
fun stackTraceContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests the query
    // ```prolog
    // ?- foo(X).
    // ```
    // where `baz/1`'s body is `X is Y + 1` with `Y` unbound, so an `instantiation_error` is raised; the resulting
    // `logicStackTrace` is `[is/2, baz/1, bar/1, foo/1, ?-/1]`.
    scenario("stack-trace/simple-stack-trace") {
        logicProgramming {
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
                        index = 0,
                    ),
                ),
                solution,
            )

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    // Same theory as [testSimpleStackTrace], but with the query wrapped in `findall(X, foo(X), L)`; the resulting
    // `logicStackTrace` gains one extra `findall/3` frame between `foo/1` and the top-level `?-/1`.
    scenario("stack-trace/double-stack-trace") {
        logicProgramming {
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
                        index = 0,
                    ),
                ),
                solution,
            )

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    // Same theory as [testSimpleStackTrace], but with the query wrapped in `findall(X, bagof(Z, foo(Z), X), L)`;
    // the resulting `logicStackTrace` gains both a `bagof/3` and a `findall/3` frame, in that order, between
    // `foo/1` and the top-level `?-/1`.
    scenario("stack-trace/triple-stack-trace") {
        logicProgramming {
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
                        index = 0,
                    ),
                ),
                solution,
            )

            assertEquals(
                listOf("is" / 2, "baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }

    // Same query as [testTripleStackTrace], but with `baz/1`'s body replaced by `throw(x)`: the resulting halt is a
    // `SystemError` wrapping the uncaught `x`, and its `logicStackTrace` does *not* include a frame for `throw/1`
    // itself — only `[baz/1, bar/1, foo/1, bagof/3, findall/3, ?-/1]` — since a `throw/1` call does not push a
    // stack frame the way a user-defined goal invocation does.
    scenario("stack-trace/throw-is-not-in-stacktrace") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = threeLayersTheory { `throw`("x") })

            val query = findall(X, bagof(Z, "foo"(Z), X), L)
            val solution = solver.solveOnce(query)
            assertSolutionEquals(
                query.halt(
                    SystemError.forUncaughtError(
                        MessageError(
                            context = DummyInstances.executionContext,
                            extraData = atomOf("x"),
                        ),
                    ),
                ),
                solution,
            )

            assertEquals(
                listOf("baz" / 1, "bar" / 1, "foo" / 1, "bagof" / 3, "findall" / 3, "?-" / 1),
                (solution as Solution.Halt).exception.logicStackTrace.map { it.indicator },
            )
        }
    }
}

private fun threeLayersTheory(errorExpression: LogicProgrammingScope.() -> Struct): Theory =
    logicProgramming {
        theoryOf(
            rule { "foo"(X) impliedBy "bar"(X) },
            rule { "bar"(X) impliedBy "baz"(X) },
            rule { "baz"(X) impliedBy errorExpression() },
        )
    }
