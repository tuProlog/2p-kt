package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown

/** Conformance tests for `fail/0`, plus the `unknown` flag's effect on resolving an undefined procedure. */
fun failContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("fail/fail") { fail yields { kotlin.collections.listOf(it.no()) } }

    // Tests the queries
    // ```prolog
    // ?- undef_pred.
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory,
    // producing exception `existence_error(procedure, undef_pred/0)`.
    scenario("fail/undef-pred") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.ERROR),
                )

            val query = atomOf("undef_pred")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("undef_pred", 0),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    case("fail/set-flag-fail") {
        ("set_prolog_flag"("unknown" and fail) and "undef_pred") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("fail/set-flag-warning") {
        ("set_prolog_flag"("unknown" and "warning") and "undef_pred") yields {
            kotlin.collections.listOf(it.no())
        }
    }
}
