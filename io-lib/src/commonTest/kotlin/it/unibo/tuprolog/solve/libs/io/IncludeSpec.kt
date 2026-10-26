package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DirectiveTestsUtils.solverInitializersWithEventsList
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IncludeSpec :
    FunSpec({
        // include/load directives become consult/1 calls, which need IOLib in the solver's runtime
        val solverFactory =
            object : SolverFactory by PrologSolverFactory {
                override val defaultRuntime: Runtime
                    get() = super.defaultRuntime + Runtime.of(IOLib)
            }

        fun theoryWithLocalInclude(loadGoal: String) =
            logicProgramming {
                val parentsPath = findResource("Parents.pl")
                theoryOf(
                    directive { loadGoal(parentsPath.toString()) },
                    fact { "someFact" },
                )
            }

        fun testLocalInclude(loadGoal: String) =
            logicProgramming {
                val theory = theoryWithLocalInclude(loadGoal)
                for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
                    val solver = solverOf(theory)

                    val expectedTheory = ExampleTheories.PARENTS + theory

                    assertEquals(listOf<Any>(), events)
                    assertTrue {
                        expectedTheory.equals(solver.staticKb + solver.dynamicKb, useVarCompleteName = false)
                    }
                }
            }

        test("local-include") {
            testLocalInclude("include")
        }

        test("local-load") {
            testLocalInclude("load")
        }

        fun theoryWithRemoteInclude(loadGoal: String) =
            logicProgramming {
                theoryOf(
                    directive { loadGoal(ExampleUrls.PARENTS) },
                    fact { "someFact" },
                )
            }

        fun testRemoteInclude(loadGoal: String) =
            logicProgramming {
                val theory = theoryWithRemoteInclude(loadGoal)
                for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
                    val solver = solverOf(theory)

                    val expectedTheory = ExampleTheories.PARENTS + theory

                    assertEquals(listOf<Any>(), events)
                    assertTrue {
                        expectedTheory.equals(solver.staticKb + solver.dynamicKb, useVarCompleteName = false)
                    }
                }
            }

        test("remote-include") {
            testRemoteInclude("include")
        }

        test("remote-load") {
            testRemoteInclude("load")
        }

        fun theoryWithMissingInclude(loadGoal: String) =
            logicProgramming {
                theoryOf(
                    directive { loadGoal("/path/to/missing.pl") },
                    fact { "someFact" },
                )
            }

        fun testMissingInclude(loadGoal: String) =
            logicProgramming {
                val theory = theoryWithMissingInclude(loadGoal)
                for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
                    val solver = solverOf(theory)

                    assertTrue { events.size == 1 }
                    assertTrue { events[0] is InitializationIssue }
                    assertTrue {
                        (events[0] as? InitializationIssue)?.message?.contains(Consult.functor) ?: false
                    }

                    assertEquals(theory, solver.staticKb + solver.dynamicKb)
                }
            }

        test("missing-include") {
            testMissingInclude("include")
        }

        test("missing-load") {
            testMissingInclude("load")
        }
    })
