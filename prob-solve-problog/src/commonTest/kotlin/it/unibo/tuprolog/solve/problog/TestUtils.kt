package it.unibo.tuprolog.solve.problog

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.theory.Theory
import kotlin.math.abs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

typealias ExpectedSolution = Pair<Struct, Double>
typealias QueryWithSolutions = Pair<Struct, Iterable<ExpectedSolution>>

object TestUtils {

    private const val doublePrecisionEpsilon = 0.01

    private fun tolerantDoubleEquals(first: Double, second: Double): Boolean {
        return abs(first / second - 1) < doublePrecisionEpsilon
    }

    fun assertQueryWithSolutions(
        theory: Theory,
        queryWithSolutions: Iterable<QueryWithSolutions>
    ) {
        val solver = ProblogProbSolverFactory.mutableSolverWithDefaultBuiltins()
        solver.loadStaticKb(theory)

        queryWithSolutions.forEach {
            val expectedSolutions = it.second.toList()
            val solutions = solver.probSolve(it.first).filter {
                s ->
                s.solution is Solution.Yes
            }.toList()

            var currentActual: ExpectedSolution? = null
            var currentExpected: ExpectedSolution? = null
            assertTrue(
                expectedSolutions.size <= solutions.size,
                "Actual solutions size should be larger or equal than the expected one"
            )
            assertFalse(
                expectedSolutions
                    .any { s ->
                        !solutions.any { sol ->
                            val solution = ExpectedSolution(
                                sol.solution.solvedQuery!!,
                                sol.probability!!
                            )
                            currentActual = solution
                            currentExpected = s
                            s.first == solution.first && tolerantDoubleEquals(s.second, solution.second)
                        }
                    },
                """Failed to assert expected solution: expected=$currentExpected, actual=$currentActual"""
            )
        }
    }

    fun assertQueryWithSolutions(
        theory: Theory,
        queryWithSolutions: QueryWithSolutions,
    ) {
        assertQueryWithSolutions(theory, listOf(queryWithSolutions))
    }
}
