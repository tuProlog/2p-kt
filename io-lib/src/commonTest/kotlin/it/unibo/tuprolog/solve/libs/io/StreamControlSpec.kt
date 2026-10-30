package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Covers `current_input/1`, `current_output/1`, `set_input/1`, `set_output/1`, `close/1`,
 * `flush_output/1`, `at_end_of_stream/0,1` and `stream_property/2` (Stream selection and control).
 */
class StreamControlSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        test("current-input-on-atom-is-domain-error") {
            // ISO: current_input(user_input) is a domain error, because a Stream can never be an alias.
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "current_input"("user_input")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            DomainError.forArgument(
                                ctx,
                                Signature("current_input", 1),
                                DomainError.Expected.STREAM_OR_ALIAS,
                                atomOf("user_input"),
                                index = 0,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }

        test("set-input-switches-the-current-input-stream") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                // Once mickey becomes the current input, arity-1 predicates read from it, and
                // current_input/1 reports mickey's own stream-term (found independently via
                // stream_property/2). The stream-term itself is implementation-dependent, so the
                // equality is checked inside the query rather than pinned down here.
                val query =
                    "set_input"("mickey") and
                        (
                            "get_char"("C1") and
                                (
                                    "get_char"("C2") and
                                        (
                                            "current_input"("S1") and
                                                ("stream_property"("S2", "alias"("mickey")) and ("S1" id "S2"))
                                        )
                                )
                        )
                val solution = solver.solve(query).toList().single()
                assertTrue(solution is Solution.Yes)
                assertEquals(Atom.of("q"), solution.valueOf("C1"))
                assertEquals(Atom.of("w"), solution.valueOf("C2"))
            }
        }

        test("set-input-preserves-other-aliases-in-the-input-store") {
            // Regression test: InputStoreImpl.setCurrent(alias) used to rebuild the whole store as just
            // {"$current" -> channel}, discarding every other registered alias - including the very one
            // just switched to.
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                val query = "set_input"("mickey") and "stream_property"("_", "alias"("mickey"))
                val solutions = solver.solve(query).toList()
                assertTrue(solutions.single().isYes)
            }
        }

        test("set-output-switches-the-current-output-stream") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "set_output"("mickey") and "put_char"("x")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("x", mickey.toString())
            }
        }

        test("close-removes-the-alias") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                val query = "close"("mickey")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
            }
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
                val query = "close"("mickey") and "get_char"("mickey", "C")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(query.halt(ExistenceError.forSourceSink(ctx, "mickey"))),
                    solutions,
                )
            }
        }

        test("close-on-missing-stream-is-existence-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "close"("donald")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(query.halt(ExistenceError.forSourceSink(ctx, "donald"))),
                    solutions,
                )
            }
        }

        test("flush-output-succeeds-on-open-output-stream") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "put_char"("mickey", "x") and "flush_output"("mickey")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
            }
        }

        test("flush-output-on-missing-stream-is-existence-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "flush_output"("donald")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(query.halt(ExistenceError.forSourceSink(ctx, "donald"))),
                    solutions,
                )
            }
        }

        test("at-end-of-stream0-on-empty-input") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "")
                val query = atomOf("at_end_of_stream")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
            }
        }

        test("at-end-of-stream1-succeeds-after-consuming-whole-stream") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "ab")))
                val query =
                    "get_char"("mickey", "C1") and
                        ("get_char"("mickey", "C2") and "at_end_of_stream"("mickey"))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes("C1" to "a", "C2" to "b")), solutions)
            }
        }

        test("at-end-of-stream0-fails-when-input-remains") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "qwerty")
                val query = atomOf("at_end_of_stream")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.no()), solutions)
            }
        }

        test("stream-property-enumerates-registered-aliases") {
            logicProgramming {
                val solver =
                    PrologSolverFactory.ioSolver(
                        namedInputs = mapOf(Pair("mickey", "qwerty")),
                        namedOutputs = mapOf(Pair("chip", StringBuilder())),
                    )
                val query = "stream_property"("_", "alias"("A"))
                val solutions = solver.solve(query).toList()
                val aliases =
                    solutions
                        .filterIsInstance<Solution.Yes>()
                        .mapNotNull { it.valueOf("A") }
                        .filterIsInstance<Atom>()
                        .map { it.value }
                        .toSet()
                assertEquals(
                    setOf("stdin", "user_input", "mickey", "stdout", "user_output", "chip", "stderr"),
                    aliases,
                )
            }
        }

        test("stream-property-on-invalid-property-is-domain-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
                val query = "stream_property"("S", "not_a_property")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(
                    listOf(
                        query.halt(
                            DomainError.forArgument(
                                ctx,
                                Signature("stream_property", 2),
                                DomainError.Expected.STREAM_PROPERTY,
                                atomOf("not_a_property"),
                                index = 1,
                            ),
                        ),
                    ),
                    solutions,
                )
            }
        }
    })
