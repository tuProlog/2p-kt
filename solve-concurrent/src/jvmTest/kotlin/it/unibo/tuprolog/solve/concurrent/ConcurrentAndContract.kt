package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentAndContract() =
    concurrentContract {
        case("and/term-is-free-variable") { "X" eq 1 and `var`("X") yields { listOf(it.no()) } }
        case("and/with-substitution") { `var`("X") and ("X" eq 1) yields { listOf(it.yes("X" to 1)) } }
        case("and/fail-is-callable") { fail and call(3) yields { listOf(it.no()) } }

        scenario("and/no-foo-is-callable") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.of(Unknown to Unknown.ERROR),
                    )

                val query = "nofoo"("X") and call("X")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            ExistenceError.forProcedure(
                                DummyInstances.executionContext,
                                Signature("nofoo", 1),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("and/true-var-callable") { "X" eq true and call("X") yields { listOf(it.yes("X" to true)) } }
    }
