package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.primitives.Consult
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestConsultImpl(private val solverFactory: SolverFactory) : TestConsult {

    private fun testConsultCorrectTheory(url: Url) {
        logicProgramming {
            val canaryTheory = theoryOf(factOf("canary"))
            val solver = solverFactory.solverWithDefaultBuiltins(
                otherLibraries = Runtime.of(IOLib),
                staticKb = canaryTheory
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

    private fun testConsultWrongTheory(url: Url) {
        logicProgramming {
            val canaryTheory = theoryOf(factOf("canary"))
            val solver = solverFactory.solverWithDefaultBuiltins(
                otherLibraries = Runtime.of(IOLib),
                staticKb = canaryTheory
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

    private fun testConsultMissingTheory(url: Url) {
        logicProgramming {
            val canaryTheory = theoryOf(factOf("canary"))
            val solver = solverFactory.solverWithDefaultBuiltins(
                otherLibraries = Runtime.of(IOLib),
                staticKb = canaryTheory
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

    override fun testApi() {
        val solver = solverFactory.solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(IOLib)
        )
        assertEquals(IOLib, solver.libraries[IOLib.alias])
        solver.assertHasPredicateInAPI(Consult)
    }

    override fun testConsultWorksLocally() {
        testConsultCorrectTheory(findResource("Parents.pl"))
    }

    override fun testConsultWorksRemotely() {
        testConsultCorrectTheory(Url.of(ExampleUrls.PARENTS))
    }

    override fun testConsultingWrongTheoryWorksLocally() {
        testConsultWrongTheory(findResource("WrongParents.pl"))
    }

    override fun testConsultingWrongTheoryWorksRemotely() {
        testConsultWrongTheory(Url.of(ExampleUrls.WRONG_PARENTS))
    }

    override fun testConsultingMissingTheoryWorksLocally() {
        testConsultMissingTheory(Url.file("/path/to/missing/resource.pl"))
    }

    override fun testConsultingMissingTheoryWorksRemotely() {
        testConsultMissingTheory(Url.http("www.missingdomain.ita", path = "/path/to/missing/resource.pl"))
    }
}
