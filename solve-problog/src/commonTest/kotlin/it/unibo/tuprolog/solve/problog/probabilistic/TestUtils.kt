package it.unibo.tuprolog.solve.problog.probabilistic

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.datatest.withTests
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

/** A named [query], expected to yield at least the [solutions] with their probabilities */
internal class QueryCase(
    val id: String,
    val query: Struct,
    val solutions: Iterable<ExpectedSolution>,
)

internal object TestUtils {
    private const val DEFAULT_DOUBLE_PRECISION = 0.01

    fun assertEqualsDouble(
        first: Double,
        second: Double,
        precision: Double = DEFAULT_DOUBLE_PRECISION,
    ): Boolean = abs(first / second - 1) < precision

    /** Registers one leaf per case, solving its query on a fresh solver loaded with the [theory] */
    fun FunSpecRootScope.queriesOn(
        id: String,
        theory: () -> Theory,
        cases: List<QueryCase>,
        enabled: Boolean = true,
    ) {
        if (enabled) {
            withTests(nameFn = { "$id/${it.id}" }, cases) { assertQueryWithSolutions(theory(), it) }
        } else {
            cases.forEach { case -> xtest("$id/${case.id}") { assertQueryWithSolutions(theory(), case) } }
        }
    }

    private fun assertQueryWithSolutions(
        theory: Theory,
        case: QueryCase,
    ) {
        val solver = ProblogSolverFactory.mutableSolverWithDefaultBuiltins()
        solver.loadStaticKb(theory)

        val expectedSolutions = case.solutions.toList()
        val solutions =
            solver
                .solve(case.query, SolveOptions.DEFAULT.setProbabilistic(true))
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
