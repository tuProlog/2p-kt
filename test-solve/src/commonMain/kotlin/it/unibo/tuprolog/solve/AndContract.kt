package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown

/** Conformance tests for `, `/2 (conjunction, `and`). */
fun andContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("and/term-is-free-variable") { "X" eq 1 and `var`("X") yields { listOf(it.no()) } }
    case("and/with-substitution") { `var`("X") and ("X" eq 1) yields { listOf(it.yes("X" to 1)) } }
    // goal
    case("and/fail-is-callable") { fail and call(3) yields { listOf(it.no()) } }

    // Tests the query
    // ```prolog
    // ?- (nofoo(X), call(X)).
    // ```
    // fails on a solver initialized with default built-ins, the `unknown` flag set to `error`, and an empty theory,
    // producing exception `existence_error(procedure, nofoo/1)`.
    scenario("and/no-foo-is-callable") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.ERROR),
                )

            val query = "nofoo"("X") and call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("nofoo", 1),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    case("and/true-var-callable") { "X" eq true and call("X") yields { listOf(it.yes("X" to true)) } }
}
