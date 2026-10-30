package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals

/**
 * Covers `put_char/1,2`, `put_code/1,2` and `nl/1` (Character input/output). All are output-only:
 * `/1` writes to the current output stream, `/2` writes to an explicit `Stream_or_alias`; `nl/1`
 * outputs a newline. `nl/0` and `write/1` are documented by the standard but not registered by this
 * library, so they are not covered here.
 */
class PutCharCodeNlSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        test("put-char1-writes-to-current-output") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "put_char"("t") and "put_char"("o")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("to", output.toString())
            }
        }

        test("put-char1-non-character-is-type-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "put_char"(1)
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            TypeError.forArgument(
                                ctx,
                                Signature("put_char", 1),
                                TypeError.Expected.CHARACTER,
                                Integer.of(1),
                                index = 0,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("put-char2-writes-to-named-stream") {
            // Regression test: put_char/2 used to write the *stream* argument (cast to Atom) instead of
            // the actual character argument.
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "put_char"("mickey", "t")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("t", mickey.toString())
            }
        }

        test("put-char2-on-input-only-stream-is-domain-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                val query = "put_char"("mickey", "t")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            DomainError.forArgument(
                                ctx,
                                Signature("put_char", 2),
                                DomainError.Expected.STREAM_TYPE,
                                atomOf("mickey"),
                                index = 0,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("put-code1-writes-to-current-output") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "put_code"(intOf('X'.code))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("X", output.toString())
            }
        }

        test("put-code1-out-of-range-is-representation-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "put_code"(intOf(-2))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            RepresentationError.of(
                                ctx,
                                Signature("put_code", 1),
                                RepresentationError.Limit.CHARACTER_CODE,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("put-code2-writes-to-named-stream") {
            // Regression test: put_code/2 used to cast the *stream* argument to Integer, which crashed
            // with a ClassCastException as soon as it was called with an alias/stream-term argument.
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "put_code"("mickey", intOf('X'.code))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("X", mickey.toString())
            }
        }

        test("nl1-writes-newline-to-named-stream") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "put_char"("mickey", "a") and ("nl"("mickey") and "put_char"("mickey", "b"))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("a\nb", mickey.toString())
            }
        }

        test("nl1-on-input-only-stream-is-domain-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                val query = "nl"("mickey")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            DomainError.forArgument(
                                ctx,
                                Signature("nl", 1),
                                DomainError.Expected.STREAM_TYPE,
                                atomOf("mickey"),
                                index = 0,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }
    })
