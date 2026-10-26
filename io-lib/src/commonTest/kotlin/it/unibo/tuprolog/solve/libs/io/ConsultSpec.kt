package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsultSpec :
    FunSpec({
        val solverFactory = PrologSolverFactory

        fun testConsultCorrectTheory(url: Url) {
            logicProgramming {
                val canaryTheory = theoryOf(factOf("canary"))
                val solver =
                    solverFactory.solverWithDefaultBuiltins(
                        otherLibraries = Runtime.of(IOLib),
                        staticKb = canaryTheory,
                    )
                assertEquals(canaryTheory, solver.staticKb)
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    assertTrue { solutions.size == 1 }
                    assertTrue { solutions.single() is Solution.Yes }
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                val expectedTheory = canaryTheory + ExampleTheories.PARENTS
                assertTrue { expectedTheory.equals(solver.staticKb, useVarCompleteName = false) }
            }
        }

        fun testConsultWrongTheory(url: Url) {
            logicProgramming {
                val canaryTheory = theoryOf(factOf("canary"))
                val solver =
                    solverFactory.solverWithDefaultBuiltins(
                        otherLibraries = Runtime.of(IOLib),
                        staticKb = canaryTheory,
                    )
                assertEquals(canaryTheory, solver.staticKb)
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    assertTrue { solutions.size == 1 }
                    solutions.single().let {
                        assertTrue { it is Solution.Halt }
                        val solution = it as Solution.Halt
                        assertTrue { solution.exception is SyntaxError }
                        println(solution.exception)
                    }
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                assertTrue { canaryTheory.equals(solver.staticKb, useVarCompleteName = false) }
            }
        }

        fun testConsultMissingTheory(url: Url) {
            logicProgramming {
                val canaryTheory = theoryOf(factOf("canary"))
                val solver =
                    solverFactory.solverWithDefaultBuiltins(
                        otherLibraries = Runtime.of(IOLib),
                        staticKb = canaryTheory,
                    )
                assertEquals(canaryTheory, solver.staticKb)
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    assertTrue { solutions.size == 1 }
                    solutions.single().let {
                        assertTrue { it is Solution.Halt }
                        val solution = it as Solution.Halt
                        assertTrue { solution.exception is SystemError }
                        println(solution.exception)
                    }
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                assertTrue { canaryTheory.equals(solver.staticKb, useVarCompleteName = false) }
            }
        }

        test("api") {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    otherLibraries = Runtime.of(IOLib),
                )
            assertEquals(IOLib, solver.libraries[IOLib.alias])
            solver.assertHasPredicateInAPI(Consult)
        }

        test("consult-works-locally") {
            testConsultCorrectTheory(findResource("Parents.pl"))
        }

        test("consult-works-remotely") {
            testConsultCorrectTheory(Url.of(ExampleUrls.PARENTS))
        }

        test("consulting-wrong-theory-works-locally") {
            // TestPrologConsult forwarded this case to testConsultWorksRemotely
            testConsultCorrectTheory(Url.of(ExampleUrls.PARENTS))
        }

        test("consulting-wrong-theory-works-remotely") {
            testConsultWrongTheory(Url.of(ExampleUrls.WRONG_PARENTS))
        }

        test("consulting-missing-theory-works-locally") {
            testConsultMissingTheory(Url.file("/path/to/missing/resource.pl"))
        }

        test("consulting-missing-theory-works-remotely") {
            testConsultMissingTheory(Url.http("www.missingdomain.ita", path = "/path/to/missing/resource.pl"))
        }
    })
