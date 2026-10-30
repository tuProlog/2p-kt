package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentIsContract() =
    concurrentContract {
        case("is/is-result") {
            "Result" `is` (numOf(3) + realOf(11.0)) yields {
                listOf(it.yes("Result" to realOf(14.0)))
            }
        }

        case("is/is-xy") {
            ("X" eq (intOf(1) + intOf(2))) and ("Y" `is` ("X" * intOf(3))) yields {
                listOf(it.yes("X" to intOf(1) + intOf(2), "Y" to intOf(9)))
            }
        }

        case("is/is-foo") { "foo" `is` intOf(77) yields { listOf(it.no()) } }

        scenario("is/is-n-number") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = intOf(77) `is` "N"
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("is", 2),
                                varOf("N"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("is/is-number-foo") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = intOf(77) `is` "foo"
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("is", 2),
                                TypeError.Expected.EVALUABLE,
                                indicatorOf("foo", 0),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("is/is-x-float") { "X" `is` float(intOf(3)) yields { listOf(it.yes("X" to realOf(3.0))) } }
    }
