package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers `write/2`, `writeq/1,2`, `write_canonical/1,2` and `write_term/2,3` (Term input/output).
 * They only differ in which fixed [it.unibo.tuprolog.core.TermFormatter] they use - `write` uses
 * unquoted operator notation, `writeq` quotes atoms that need it, `write_canonical` additionally
 * ignores operator notation - except `write_term`, which takes the formatting options explicitly.
 */
class TestWriteTerm {
    private val ctx = DummyInstances.executionContext

    @Test
    fun testWrite2UsesUnquotedOperatorNotation() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "write"("mickey", structOf("+", 1, 2))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("1 + 2", mickey.toString())
        }
    }

    @Test
    fun testWrite2DoesNotQuoteAtoms() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "write"("mickey", "A b")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("A b", mickey.toString())
        }
    }

    @Test
    fun testWrite2OnInputOnlyStreamIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "")))
            val query = "write"("mickey", "a")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            ctx,
                            Signature("write", 2),
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
    fun testWriteEq1QuotesAtomsThatNeedIt() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "writeq"("A b")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'A b'", output.toString())
        }
    }

    @Test
    fun testWriteEq2QuotesAtomsOnNamedStream() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "writeq"("mickey", "A b")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'A b'", mickey.toString())
        }
    }

    @Test
    fun testWriteCanonical1IgnoresOperatorNotation() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "write_canonical"(structOf("+", 1, 2))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'+'(1, 2)", output.toString())
        }
    }

    @Test
    fun testWriteCanonical2IgnoresOperatorNotationOnNamedStream() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "write_canonical"("mickey", structOf("+", 1, 2))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'+'(1, 2)", mickey.toString())
        }
    }

    @Test
    fun testWriteTerm2HonoursQuotedOption() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "write_term"("A b", logicListOf("quoted"(true)))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'A b'", output.toString())
        }
    }

    @Test
    fun testWriteTerm2HonoursIgnoreOpsOption() {
        logicProgramming {
            val output = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(output = output)
            val query = "write_term"(structOf("+", 1, 2), logicListOf("ignore_ops"(true)))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("+(1, 2)", output.toString())
        }
    }

    @Test
    fun testWriteTerm2InvalidOptionListIsTypeError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver()
            val query = "write_term"("a", "X")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            ctx,
                            Signature("write_term", 2),
                            TypeError.Expected.LIST,
                            varOf("X"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testWriteTerm3WritesTermToNamedStreamWithOptions() {
        // Regression test: write_term/3 used to write the *stream* argument as the term, and read
        // the *term* argument's list of options (rather than the actual Options argument).
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val query = "write_term"("mickey", "A b", logicListOf("quoted"(true)))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes()), solutions)
            assertEquals("'A b'", mickey.toString())
        }
    }

    @Test
    fun testWriteTerm3InvalidOptionIsDomainError() {
        logicProgramming {
            val mickey = StringBuilder()
            val solver = ClassicSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
            val badOption = "quoted"("not_a_bool")
            val query = "write_term"("mickey", "a", logicListOf(badOption))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forTerm(
                            ctx,
                            DomainError.Expected.WRITE_OPTION,
                            badOption,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
