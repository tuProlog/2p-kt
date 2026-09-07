package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers `put_char/1,2`, `put_code/1,2` and `nl/1` (Character input/output). All are output-only:
 * `/1` writes to the current output stream, `/2` writes to an explicit `Stream_or_alias`; `nl/1`
 * outputs a newline. `nl/0` and `write/1` are documented by the standard but not registered by this
 * library, so they are not covered here.
 */
class TestPutCharCodeNl {
    private val ctx = DummyInstances.executionContext

    @Test
    fun testPutChar1WritesToCurrentOutput() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "put_char"("t") and "put_char"("o")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("to", output.toString())
        }
    }

    @Test
    fun testPutChar1NonCharacterIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
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

    @Test
    fun testPutChar2WritesToNamedStream() {
        // Regression test: put_char/2 used to write the *stream* argument (cast to Atom) instead of
        // the actual character argument.
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "put_char"("mickey", "t")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("t", mickey.toString())
        }
    }

    @Test
    fun testPutChar2OnInputOnlyStreamIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
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

    @Test
    fun testPutCode1WritesToCurrentOutput() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "put_code"(intOf('X'.code))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("X", output.toString())
        }
    }

    @Test
    fun testPutCode1OutOfRangeIsRepresentationError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
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

    @Test
    fun testPutCode2WritesToNamedStream() {
        // Regression test: put_code/2 used to cast the *stream* argument to Integer, which crashed
        // with a ClassCastException as soon as it was called with an alias/stream-term argument.
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "put_code"("mickey", intOf('X'.code))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("X", mickey.toString())
        }
    }

    @Test
    fun testNl1WritesNewlineToNamedStream() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "put_char"("mickey", "a") and ("nl"("mickey") and "put_char"("mickey", "b"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("a\nb", mickey.toString())
        }
    }

    @Test
    fun testNl1OnInputOnlyStreamIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
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
}
