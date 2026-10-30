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

fun concurrentNotProvableContract() =
    concurrentContract {
        case("not-provable/np-true") { "not"(true) yields { listOf(it.no()) } }
        case("not-provable/np-cut") { "not"("!") yields { listOf(it.no()) } }
        case("not-provable/np-cut-fail") { "not"(("!" and fail)) yields { listOf(it.yes()) } }

        scenario("not-provable/or-not-cut-fail") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = ((("X" eq 1) or ("X" eq 2)) and "not"(("!" and fail)))
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes("X" to 1),
                            query.yes("X" to 2),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("not-provable/np-equals") { "not"(4 eq 5) yields { listOf(it.yes()) } }

        scenario("not-provable/np-num") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = "not"(3)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                TypeError.Expected.CALLABLE,
                                numOf(3),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("not-provable/npx") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = "not"("X")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forGoal(
                                DummyInstances.executionContext,
                                errorSignature,
                                varOf("X"),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }

private val errorSignature = Signature("ensure_executable", 1)
