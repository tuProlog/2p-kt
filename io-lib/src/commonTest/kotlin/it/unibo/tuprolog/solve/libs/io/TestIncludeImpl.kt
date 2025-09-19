package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DirectiveTestsUtils.solverInitializersWithEventsList
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestIncludeImpl(
    private val solverFactory: SolverFactory,
) : TestInclude {
    private fun theoryWithLocalInclude(loadGoal: String) =
        logicProgramming {
            val parentsPath = findResource("Parents.pl")
            theoryOf(
                directive { loadGoal(parentsPath.toString()) },
                fact { "someFact" },
            )
        }

    private fun testLocalInclude(loadGoal: String) =
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

    override fun testLocalInclude() {
        testLocalInclude("include")
    }

    override fun testLocalLoad() {
        testLocalInclude("load")
    }

    private fun theoryWithRemoteInclude(loadGoal: String) =
        logicProgramming {
            theoryOf(
                directive { loadGoal(ExampleUrls.PARENTS) },
                fact { "someFact" },
            )
        }

    private fun testRemoteInclude(loadGoal: String) =
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

    override fun testRemoteInclude() {
        testRemoteInclude("include")
    }

    override fun testRemoteLoad() {
        testRemoteInclude("load")
    }

    private fun theoryWithMissingInclude(loadGoal: String) =
        logicProgramming {
            theoryOf(
                directive { loadGoal("/path/to/missing.pl") },
                fact { "someFact" },
            )
        }

    private fun testMissingInclude(loadGoal: String) =
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

    override fun testMissingInclude() {
        testMissingInclude("include")
    }

    override fun testMissingLoad() {
        testMissingInclude("load")
    }
}
