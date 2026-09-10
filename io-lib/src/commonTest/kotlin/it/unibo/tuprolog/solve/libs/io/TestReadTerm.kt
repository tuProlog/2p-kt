package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Covers `read/1,2` and `read_term/2,3` (Term input/output). `read/1,2` behave like `read_term`
 * with an empty read-options list; `read_term` additionally instantiates `variables/1`,
 * `variable_names/1` and `singletons/1` read-options.
 */
class TestReadTerm {
    private val ctx = DummyInstances.executionContext

    @Test
    fun testRead1ParsesATerm() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "foo(a,b).")
            val query = "read"("T")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("T" to structOf("foo", "a", "b"))), solutions)
        }
    }

    @Test
    fun testRead1UnificationFails() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "foo(a,b).")
            val query = "read"(structOf("foo", "X", "a"))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.no()), solutions)
        }
    }

    @Test
    fun testRead1OnEmptyStreamFailsInsteadOfUnifyingWithEndOfFile() {
        // Known gap: ISO mandates that read/1 succeeds with Term = end_of_file once the stream is
        // exhausted; this implementation instead just fails (readTermAndReply bails out as soon as
        // the term channel reports no term is available, without special-casing end-of-file).
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "")
            val query = "read"("T")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.no()), solutions)
        }
    }

    @Test
    fun testRead1OnMalformedInputIsSyntaxError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "foo(a,b")
            val query = "read"("T")
            val solution = solver.solve(query).toList().single()
            assertTrue(solution is Solution.Halt)
            assertTrue(solution.exception is SyntaxError)
        }
    }

    @Test
    fun testRead2ParsesFromNamedStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
            val query = "read"("mickey", "T")
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("T" to structOf("foo", "a", "b"))), solutions)
        }
    }

    @Test
    fun testReadTerm2VariablesOption() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(stdIn = "foo(A,B).")
            val query = "read_term"("T", logicListOf("variables"("Vs")))
            val solutions = solver.solve(query).toList()
            val expectedT = structOf("foo", varOf("A"), varOf("B"))
            assertSolutionEquals(
                listOf(query.yes("T" to expectedT, "Vs" to logicListOf(varOf("A"), varOf("B")))),
                solutions,
            )
        }
    }

    @Test
    fun testReadTerm2SingletonsOption() {
        logicProgramming {
            // A appears twice (not a singleton), B appears once (a singleton).
            val solver = ClassicSolverFactory.ioSolver(stdIn = "foo(A,A,B).")
            val query = "read_term"("T", logicListOf("singletons"("Ss")))
            val solutions = solver.solve(query).toList()
            val expectedSingleton = Struct.of("=", Atom.of("B"), Var.of("B"))
            assertSolutionEquals(
                listOf(
                    query.yes(
                        "T" to structOf("foo", varOf("A"), varOf("A"), varOf("B")),
                        "Ss" to logicListOf(expectedSingleton),
                    ),
                ),
                solutions,
            )
        }
    }

    @Test
    fun testReadTerm3ReadsFromNamedStream() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
            val query =
                "read_term"(
                    "mickey",
                    "T",
                    it.unibo.tuprolog.core.List
                        .empty(),
                )
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(listOf(query.yes("T" to structOf("foo", "a", "b"))), solutions)
        }
    }

    @Test
    fun testReadTerm3InvalidOptionIsDomainError() {
        logicProgramming {
            val solver = ClassicSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
            val badOption = "bogus_option"("X")
            val query = "read_term"("mickey", "T", logicListOf(badOption))
            val solutions = solver.solve(query).toList()
            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forTerm(
                            ctx,
                            DomainError.Expected.READ_OPTION,
                            badOption,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
