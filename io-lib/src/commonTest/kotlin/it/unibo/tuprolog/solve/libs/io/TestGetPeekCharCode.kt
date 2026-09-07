package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

/**
 * Covers `get_char/1,2`, `peek_char/1,2`, `get_code/1,2` and `peek_code/1,2` against the ISO
 * standard ("Prolog: The Standard", Character input/output). `get_*` advances the stream position,
 * `peek_*` leaves it untouched; both come in a "current input stream" (arity 1) and an explicit
 * "Stream_or_alias" (arity 2) flavour.
 */
class TestGetPeekCharCode {
    private val ctx = DummyInstances.executionContext

    @Test
    fun testGetChar1ReadsAndAdvances() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_char"("C1") and "get_char"("C2")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("C1" to "q", "C2" to "w")), solutions)
        }
    }

    @Test
    fun testGetChar1AtEndOfFile() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = "get_char"("Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Char" to "end_of_file")), solutions)
        }
    }

    @Test
    fun testGetChar1UnificationFails() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_char"("a")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.no()), solutions)
        }
    }

    @Test
    fun testGetChar1NonCharacterIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_char"(123)
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            ctx,
                            Signature("get_char", 1),
                            TypeError.Expected.IN_CHARACTER,
                            Integer.of(123),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testGetChar1MultiCharAtomIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_char"("ab")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            ctx,
                            Signature("get_char", 1),
                            TypeError.Expected.IN_CHARACTER,
                            Atom.of("ab"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testGetChar2ReadsFromNamedStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "get_char"("mickey", "Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Char" to "q")), solutions)
        }
    }

    @Test
    fun testGetChar2OnMissingStreamIsExistenceError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
            val query = "get_char"("donald", "Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(query.halt(ExistenceError.forSourceSink(ctx, "donald"))),
                solutions,
            )
        }
    }

    @Test
    fun testGetChar2OnOutputStreamIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", StringBuilder())))
            val query = "get_char"("mickey", "Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            ctx,
                            Signature("get_char", 2),
                            DomainError.Expected.STREAM_TYPE,
                            Atom.of("mickey"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testGetChar2UnboundStreamIsInstantiationError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
            val query = "get_char"("Stream", "Char")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            ctx,
                            Signature("get_char", 2),
                            varOf("Stream"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testPeekChar1DoesNotAdvanceStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "peek_char"("P1") and ("peek_char"("P2") and "get_char"("G1"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("P1" to "q", "P2" to "q", "G1" to "q")), solutions)
        }
    }

    @Test
    fun testPeekChar2DoesNotAdvanceNamedStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "peek_char"("mickey", "P1") and "get_char"("mickey", "G1")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("P1" to "q", "G1" to "q")), solutions)
        }
    }

    @Test
    fun testGetCode1ReadsCharacterCode() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_code"("Code")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Code" to 'q'.code)), solutions)
        }
    }

    // testGetCode1AtEndOfFileYieldsMinusOne lives in TestGetPeekCharCodeJvm: on JS, building the
    // expected `-1` through the DSL hits an unrelated pre-existing bug in
    // it.unibo.tuprolog.utils.NumberTypeTester.isInteger ("[0-9]+" doesn't match a leading '-', so
    // -1 is misclassified as a real number, 'Code' <- -1.0, instead of an integer). Not one of the
    // two :solve bugs asked for here, so left as a JVM-only regression test rather than fixed.

    @Test
    fun testGetCode1NonIntegerIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_code"("a")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            ctx,
                            Signature("get_code", 1),
                            TypeError.Expected.INTEGER,
                            Atom.of("a"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testGetCode1OutOfRangeIntegerIsRepresentationError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "get_code"(intOf(Char.MAX_VALUE.code + 1))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        RepresentationError.of(
                            ctx,
                            Signature("get_code", 1),
                            RepresentationError.Limit.CHARACTER_CODE,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testGetCode2ReadsFromNamedStream() {
        // Regression test: get_code/2 used to check/unify the *stream* argument as if it were the
        // character-code argument, so this always failed or errored (never touching the actual code).
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "get_code"("mickey", "Code")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("Code" to 'q'.code)), solutions)
        }
    }

    @Test
    fun testGetCode2NonIntegerCodeIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "get_code"("mickey", "a")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            ctx,
                            Signature("get_code", 2),
                            TypeError.Expected.INTEGER,
                            Atom.of("a"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testPeekCode1DoesNotAdvanceStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "qwerty")
            val query = "peek_code"("P1") and ("peek_code"("P2") and "get_code"("G1"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(query.yes("P1" to 'q'.code, "P2" to 'q'.code, "G1" to 'q'.code)),
                solutions,
            )
        }
    }

    @Test
    fun testPeekCode2ReadsFromNamedStreamWithoutAdvancing() {
        // Regression test: peek_code/2 had the same stream/code argument mix-up as get_code/2.
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "qwerty")))
            val query = "peek_code"("mickey", "P1") and "get_code"("mickey", "G1")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("P1" to 'q'.code, "G1" to 'q'.code)), solutions)
        }
    }
}
