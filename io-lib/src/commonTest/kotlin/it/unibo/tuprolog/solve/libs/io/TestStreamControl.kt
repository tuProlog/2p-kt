package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Covers `current_input/1`, `current_output/1`, `set_input/1`, `set_output/1`, `close/1`,
 * `flush_output/1`, `at_end_of_stream/0,1` and `stream_property/2` (Stream selection and control).
 */
class TestStreamControl {
    private val ctx = DummyInstances.executionContext

    @Test
    fun testCurrentInputOnAtomIsDomainError() {
        // ISO: current_input(user_input) is a domain error, because a Stream can never be an alias.
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
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

    @Test
    fun testSetInputSwitchesTheCurrentInputStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
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

    @Test
    fun testSetInputPreservesOtherAliasesInTheInputStore() {
        // Regression test: InputStoreImpl.setCurrent(alias) used to rebuild the whole store as just
        // {"$current" -> channel}, discarding every other registered alias - including the very one
        // just switched to.
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "set_input"("mickey") and "stream_property"("_", "alias"("mickey"))
            val solutions = solver.solve(query).toList()
            assertTrue(solutions.single().isYes)
        }
    }

    @Test
    fun testSetOutputSwitchesTheCurrentOutputStream() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "set_output"("mickey") and "put_char"("x")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("x", mickey.toString())
        }
    }

    @Test
    fun testCloseRemovesTheAlias() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "close"("mickey")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
        }
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "close"("mickey") and "get_char"("mickey", "C")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(query.halt(ExistenceError.forSourceSink(ctx, "mickey"))),
                solutions,
            )
        }
    }

    @Test
    fun testCloseOnMissingStreamIsExistenceError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
            val query = "close"("donald")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(query.halt(ExistenceError.forSourceSink(ctx, "donald"))),
                solutions,
            )
        }
    }

    @Test
    fun testFlushOutputSucceedsOnOpenOutputStream() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "put_char"("mickey", "x") and "flush_output"("mickey")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
        }
    }

    @Test
    fun testFlushOutputOnMissingStreamIsExistenceError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
            val query = "flush_output"("donald")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(query.halt(ExistenceError.forSourceSink(ctx, "donald"))),
                solutions,
            )
        }
    }

    @Test
    fun testAtEndOfStream0OnEmptyInput() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = atomOf("at_end_of_stream")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
        }
    }

    @Test
    fun testAtEndOfStream1SucceedsAfterConsumingWholeStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "ab")))
            val query =
                "get_char"("mickey", "C1") and
                    ("get_char"("mickey", "C2") and "at_end_of_stream"("mickey"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("C1" to "a", "C2" to "b")), solutions)
        }
    }

    @Test
    fun testAtEndOfStream0FailsWhenInputRemains() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = atomOf("at_end_of_stream")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.no()), solutions)
        }
    }

    @Test
    fun testStreamPropertyEnumeratesRegisteredAliases() {
        logicProgramming {
            val solver =
                ClassicSolverFactory.ioSolver(
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

    @Test
    fun testStreamPropertyOnInvalidPropertyIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
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
}
