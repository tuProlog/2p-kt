package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.yes
import kotlin.test.assertEquals

/**
 * Covers `write/2`, `writeq/1,2`, `write_canonical/1,2` and `write_term/2,3` (Term input/output).
 * They only differ in which fixed [it.unibo.tuprolog.core.TermFormatter] they use - `write` uses
 * unquoted operator notation, `writeq` quotes atoms that need it, `write_canonical` additionally
 * ignores operator notation - except `write_term`, which takes the formatting options explicitly.
 */
class WriteTermSpec :
    FunSpec({
        val ctx = DummyInstances.executionContext

        test("write2-uses-unquoted-operator-notation") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "write"("mickey", structOf("+", 1, 2))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("1 + 2", mickey.toString())
            }
        }

        test("write2-does-not-quote-atoms") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "write"("mickey", "A b")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("A b", mickey.toString())
            }
        }

        test("write2-on-input-only-stream-is-domain-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver(namedInputs = mapOf(Pair("mickey", "")))
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

        test("write-eq1-quotes-atoms-that-need-it") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "writeq"("A b")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'A b'", output.toString())
            }
        }

        test("write-eq2-quotes-atoms-on-named-stream") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "writeq"("mickey", "A b")
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'A b'", mickey.toString())
            }
        }

        test("write-canonical1-ignores-operator-notation") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "write_canonical"(structOf("+", 1, 2))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'+'(1, 2)", output.toString())
            }
        }

        test("write-canonical2-ignores-operator-notation-on-named-stream") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "write_canonical"("mickey", structOf("+", 1, 2))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'+'(1, 2)", mickey.toString())
            }
        }

        test("write-term2-honours-quoted-option") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "write_term"("A b", logicListOf("quoted"(true)))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'A b'", output.toString())
            }
        }

        test("write-term2-honours-ignore-ops-option") {
            logicProgramming {
                val output = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(output = output)
                val query = "write_term"(structOf("+", 1, 2), logicListOf("ignore_ops"(true)))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("+(1, 2)", output.toString())
            }
        }

        test("write-term2-invalid-option-list-is-type-error") {
            logicProgramming {
                val solver = PrologSolverFactory.ioSolver()
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

        test("write-term3-writes-term-to-named-stream-with-options") {
            // Regression test: write_term/3 used to write the *stream* argument as the term, and read
            // the *term* argument's list of options (rather than the actual Options argument).
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
                val query = "write_term"("mickey", "A b", logicListOf("quoted"(true)))
                val solutions = solver.solve(query).toList()
                assertSolutionEquals(listOf(query.yes()), solutions)
                assertEquals("'A b'", mickey.toString())
            }
        }

        test("write-term3-invalid-option-is-domain-error") {
            logicProgramming {
                val mickey = StringBuilder()
                val solver = PrologSolverFactory.ioSolver(namedOutputs = mapOf(Pair("mickey", mickey)))
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
    })
