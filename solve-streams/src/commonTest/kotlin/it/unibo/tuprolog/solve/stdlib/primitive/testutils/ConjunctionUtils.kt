package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Conjunction]
 *
 * @author Enrico
 */
internal object ConjunctionUtils {

    internal val trueAndTrueSolveRequest = prolog { createSolveRequest("true" and "true") }

    internal val twoMatchesDB = prolog { theory({ "f"("a") }, { "f"("b") }) }
    internal val myRequestToSolutions = prolog {
        ktListOf(
            ("f"("A") and "f"("B")).hasSolutions(
                { yes("A" to "a", "B" to "a") },
                { yes("A" to "a", "B" to "b") },
                { yes("A" to "b", "B" to "a") },
                { yes("A" to "b", "B" to "b") }
            )
        )
    }

    /** Requests that should fail */
    internal val failedRequests = prolog {
        ktListOf(
            "true" and "fail",
            "fail" and "true",
            "true" and "true" and "fail"
        ).map { createSolveRequest(it) }
    }
}
