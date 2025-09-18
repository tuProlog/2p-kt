package it.unibo.tuprolog.solve.problog.probabilistic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.theory.Theory
import kotlin.math.abs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

typealias ExpectedSolution = Pair<Struct, Double>
typealias QueryWithSolutions = Pair<Struct, Iterable<ExpectedSolution>>

internal object TestUtils {
    private const val DEFAULT_DOUBLE_PRECISION = 0.01

    fun assertEqualsDouble(
        first: Double,
        second: Double,
        precision: Double = DEFAULT_DOUBLE_PRECISION,
    ): Boolean = abs(first / second - 1) < precision

    fun assertQueryWithSolutions(
        theory: Theory,
        queryWithSolutions: Iterable<QueryWithSolutions>,
    ) {
        val solver = ProblogSolverFactory.mutableSolverWithDefaultBuiltins()
        solver.loadStaticKb(theory)

        queryWithSolutions.forEach {
            val expectedSolutions = it.second.toList()
            val solutions =
                solver
                    .solve(it.first, SolveOptions.DEFAULT.setProbabilistic(true))
                    .filterIsInstance<Solution.Yes>()
                    .toList()

            var currentActual: ExpectedSolution? = null
            var currentExpected: ExpectedSolution? = null
            assertTrue(
                expectedSolutions.size <= solutions.size,
                "Actual solutions size should be larger or equal than the expected one",
            )
            assertFalse(
                expectedSolutions
                    .any { s ->
                        !solutions.any { sol ->
                            val solution =
                                ExpectedSolution(
                                    sol.solvedQuery,
                                    sol.probability,
                                )
                            currentActual = solution
                            currentExpected = s
                            s.first == solution.first &&
                                assertEqualsDouble(
                                    s.second,
                                    solution.second,
                                )
                        }
                    },
                """Failed to assert expected solution: expected=$currentExpected, actual=$currentActual""",
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
