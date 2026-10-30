package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yields

fun concurrentFailContract() =
    concurrentContract {
        case("fail/fail") { fail yields { listOf(it.no()) } }

        scenario("fail/undef-pred") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.of(Unknown to Unknown.ERROR),
                    )

                val query = atomOf("undef_pred")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            ExistenceError.forProcedure(
                                DummyInstances.executionContext,
                                Signature("undef_pred", 0),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("fail/set-flag-fail") {
            ("set_prolog_flag"("unknown" and fail) and "undef_pred") yields {
                listOf(it.no())
            }
        }

        case("fail/set-flag-warning") {
            ("set_prolog_flag"("unknown" and "warning") and "undef_pred") yields {
                listOf(it.no())
            }
        }
    }
