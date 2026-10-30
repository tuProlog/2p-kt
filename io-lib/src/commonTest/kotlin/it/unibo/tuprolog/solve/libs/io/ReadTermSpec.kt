package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertTrue

/**
 * Covers `read/1,2` and `read_term/2,3` (Term input/output). `read/1,2` behave like `read_term`
 * with an empty read-options list; `read_term` additionally instantiates `variables/1`,
 * `variable_names/1` and `singletons/1` read-options.
 */
class ReadTermSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        test("read1-parses-a-term") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "foo(a,b).")
                val query = "read"("T")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes("T" to structOf("foo", "a", "b"))), solutions)
            }
        }

        test("read1-unification-fails") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "foo(a,b).")
                val query = "read"(structOf("foo", "X", "a"))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.no()), solutions)
            }
        }

        test("read1-on-empty-stream-fails-instead-of-unifying-with-end-of-file") {
            // Known gap: ISO mandates that read/1 succeeds with Term = end_of_file once the stream is
            // exhausted; this implementation instead just fails (readTermAndReply bails out as soon as
            // the term channel reports no term is available, without special-casing end-of-file).
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "")
                val query = "read"("T")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.no()), solutions)
            }
        }

        test("read1-on-malformed-input-is-syntax-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "foo(a,b")
                val query = "read"("T")
                val solution = solver.solve(query).toList().single()
                assertTrue(solution is Solution.Halt)
                assertTrue(solution.exception is SyntaxError)
            }
        }

        test("read2-parses-from-named-stream") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
                val query = "read"("mickey", "T")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes("T" to structOf("foo", "a", "b"))), solutions)
            }
        }

        test("read-term2-variables-option") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(stdIn = "foo(A,B).")
                val query = "read_term"("T", logicListOf("variables"("Vs")))
                val solutions = solver.solve(query).toList()
                val expectedT = structOf("foo", varOf("A"), varOf("B"))
                assertSolutionEquals(
                    listOf(query.yes("T" to expectedT, "Vs" to logicListOf(varOf("A"), varOf("B")))),
                    solutions,
                )
            }
        }

        test("read-term2-singletons-option") {
            logicProgramming {
                // A appears twice (not a singleton), B appears once (a singleton).
                val solver = PrologSolverFactory.ioSolver(stdIn = "foo(A,A,B).")
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

        test("read-term3-reads-from-named-stream") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
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

        test("read-term3-invalid-option-is-domain-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "foo(a,b).")))
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
    })
