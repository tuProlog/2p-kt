package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yields

fun concurrentCatchAndThrowContract() =
    concurrentContract {
        scenario("catch-and-throw/catch-throw") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = (catch(true, C, write("something")) and `throw`("blabla"))
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            SystemError.forUncaughtException(
                                DummyInstances.executionContext,
                                atomOf("blabla"),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        case("catch-and-throw/catch-fail") {
            catch(number_chars("A", "L"), "error"("instantiation_error", `_`), fail) yields {
                listOf(it.no())
            }
        }
    }
