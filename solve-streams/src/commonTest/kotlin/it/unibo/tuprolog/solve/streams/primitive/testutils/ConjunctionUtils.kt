package it.unibo.tuprolog.solve.streams.primitive.testutils

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes

/**
 * Utils singleton to help testing [Conjunction]
 *
 * @author Enrico
 */
internal object ConjunctionUtils {
    internal val trueAndTrueSolveRequest = logicProgramming { createSolveRequest("true" and "true") }

    internal val twoMatchesDB = logicProgramming { theory({ "f"("a") }, { "f"("b") }) }
    internal val myRequestToSolutions =
        logicProgramming {
            listOf(
                ("f"("A") and "f"("B")).hasSolutions(
                    { yes("A" to "a", "B" to "a") },
                    { yes("A" to "a", "B" to "b") },
                    { yes("A" to "b", "B" to "a") },
                    { yes("A" to "b", "B" to "b") },
                ),
            )
        }

    /** Requests that should fail */
    internal val failedRequests =
        logicProgramming {
            listOf(
                "true" and "fail",
                "fail" and "true",
                "true" and "true" and "fail",
            ).map { createSolveRequest(it) }
        }
}
