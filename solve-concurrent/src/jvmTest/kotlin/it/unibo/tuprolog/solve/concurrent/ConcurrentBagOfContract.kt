@file:Suppress("LongMethod") // a case table

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

fun concurrentBagOfContract() =
    concurrentContract {
        case("bag-of/bag-x-in-different-values") {
            bagof("X", ("X" eq 1) or ("X" eq 2), "S") yields {
                listOf(it.yes("S" to logicListOf(1, 2)))
            }
        }

        case("bag-of/bag-of-find-x") {
            bagof("X", ("X" eq 1) or ("X" eq 2), "X") yields {
                listOf(it.yes("X" to logicListOf(1, 2)))
            }
        }

        case("bag-of/bag-of-yxz") {
            bagof("X", ("X" eq "Y") or ("X" eq "Z"), "L") yields {
                listOf(it.yes("L" to logicListOf("Y", "Z")))
            }
        }

        case("bag-of/bag-of-fail") { bagof("X", fail, "L") yields { listOf(it.no()) } }

        case("bag-of/bag-of-same-as-findall") {
            bagof("X", "Y" sup (("X" eq 1) or ("Y" eq 2)), "S") yields {
                listOf(it.yes("S" to logicListOf(1)))
            }
        }

        scenario("bag-of/bag-of-instance-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = bagof("X", "G", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("bagof", 3),
                                varOf("G"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("bag-of/bag-of-type-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = bagof("X", 1, "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("bagof", 3),
                                TypeError.Expected.CALLABLE,
                                numOf(1),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
