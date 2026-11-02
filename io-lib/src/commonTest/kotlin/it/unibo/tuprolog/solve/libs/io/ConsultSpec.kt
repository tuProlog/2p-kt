package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory

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
                solver.staticKb shouldBe canaryTheory
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    solutions.size shouldBe 1
                    solutions.single().shouldBeInstanceOf<Solution.Yes>()
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                val expectedTheory = canaryTheory + ExampleTheories.PARENTS
                expectedTheory.equals(solver.staticKb, useVarCompleteName = false).shouldBeTrue()
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
                solver.staticKb shouldBe canaryTheory
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    solutions.size shouldBe 1
                    solutions.single().let {
                        val solution = it.shouldBeInstanceOf<Solution.Halt>()
                        solution.exception.shouldBeInstanceOf<SyntaxError>()
                        println(solution.exception)
                    }
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                canaryTheory.equals(solver.staticKb, useVarCompleteName = false).shouldBeTrue()
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
                solver.staticKb shouldBe canaryTheory
                val query = consult(url.toString())
                val solutions = solver.solve(query).toList()
                try {
                    solutions.size shouldBe 1
                    solutions.single().let {
                        val solution = it.shouldBeInstanceOf<Solution.Halt>()
                        solution.exception.shouldBeInstanceOf<SystemError>()
                        println(solution.exception)
                    }
                } catch (e: AssertionError) {
                    throw AssertionError("Unexpected solutions: $solutions")
                }
                canaryTheory.equals(solver.staticKb, useVarCompleteName = false).shouldBeTrue()
            }
        }

        test("api") {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    otherLibraries = Runtime.of(IOLib),
                )
            solver.libraries[IOLib.alias] shouldBe IOLib
            solver.assertHasPredicateInAPI(Consult)
        }

        test("consult-works-locally") {
            testConsultCorrectTheory(findResource("Parents.pl"))
        }

        test("consult-works-remotely") {
            testConsultCorrectTheory(Url.of(ExampleUrls.PARENTS))
        }

        test("consulting-wrong-theory-works-locally") {
            testConsultWrongTheory(findResource("WrongParents.pl"))
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
