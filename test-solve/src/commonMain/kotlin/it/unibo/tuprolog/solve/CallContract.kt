package it.unibo.tuprolog.solve

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** The theory [callNCases] run on. */
val CALL_N_THEORY =
    arrayOf(
        "inc(N, M) :- M is N + 1",
        "map([], _, [])",
        "map([H|T], P, [H2|T2]) :- call(P, H, H2), map(T, P, T2)",
        "p(1, 2, 3, 4, 5, 6, 7)",
    )

/** `call/2..8` (ISO/IEC 13211-1:1995/Cor.2:2012): queries on [CALL_N_THEORY], and their answers. */
val callNCases =
    listOf(
        "atom-goal" to ("call(inc, 1, X)" to "yes: X = 2"),
        "compound-goal" to ("call(inc(1), X)" to "yes: X = 2"),
        "call-as-goal" to ("call(call, inc, 1, X)" to "yes: X = 2"),
        "higher-order-predicate" to ("map([1, 2], inc, L)" to "yes: L = [2, 3]"),
        "seven-extra-arguments" to ("call(p, 1, 2, 3, 4, 5, 6, X)" to "yes: X = 7"),
        "failing-goal" to ("call(inc, 1, 3)" to "no"),
        "unbound-goal" to ("call(_, a)" to "halt: instantiation_error"),
        "non-callable-goal" to ("call(1, a)" to "halt: type_error(callable, 1)"),
    )

/** Conformance tests for the ISO `call/1` built-in, and for `call/2..8`. */
fun callContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("call", 1),
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("call/call-cut") { call("!") yields { listOf(it.yes()) } }
    case("call/call-fail") { call(fail) yields { listOf(it.no()) } }
    case("call/call-fail-x") { call(fail and "X") yields { listOf(it.no()) } }
    case("call/call-fail-call") { call(fail and call(1)) yields { listOf(it.no()) } }

    case("call/call-write-x") {
        call(write(3) and "X") yields {
            listOf(
                it.halt(
                    InstantiationError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        varOf("X"),
                    ),
                ),
            )
        }
    }

    case("call/call-write-call") {
        call(write(3) and call(1)) yields {
            listOf(
                it.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(1),
                    ),
                ),
            )
        }
    }

    case("call/call-x") {
        call("X") yields {
            listOf(
                it.halt(
                    InstantiationError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        varOf("X"),
                    ),
                ),
            )
        }
    }

    case("call/call-one") {
        call(1) yields {
            listOf(
                it.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(1),
                    ),
                ),
            )
        }
    }

    // Tests the queries
    // ```prolog
    // ?- call((fail,1)).
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory,
    // producing exception `type_error(callable,(fail,1))`.
    scenario("call/call-fail-one") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            fail and 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    // Tests the queries
    // ```prolog
    // ?- call((write(3),1)).
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory,
    // producing exception `type_error(callable,(write(3),1))`.
    scenario("call/call-write-one") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            write(3) and 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    // Tests the queries
    // ```prolog
    // ?- call((1; true)).
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory,
    // producing exception `type_error(callable,(1; true))`.
    scenario("call/call-true") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(1 or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            (1 or true),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    callNCases.forEach { (id, case) ->
        val (query, answer) = case
        scenario("call/n/$id") { solverFactory.answers(query, *CALL_N_THEORY) shouldBe listOf(answer) }
    }
}
