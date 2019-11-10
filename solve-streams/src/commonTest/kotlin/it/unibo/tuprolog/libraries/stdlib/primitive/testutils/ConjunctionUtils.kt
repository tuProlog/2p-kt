package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yesSolution
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
                ("f"("A") and "f"("B")).run {
                    to(ktListOf(
                            yesSolution(Substitution.of("A" to "a", "B" to "a")),
                            yesSolution(Substitution.of("A" to "a", "B" to "b")),
                            yesSolution(Substitution.of("A" to "b", "B" to "a")),
                            yesSolution(Substitution.of("A" to "b", "B" to "b"))
                    ))
                }
        )
    }

    /** Requests that should fail */
    internal val failedRequests = prolog {
        ktListOf(
                createSolveRequest("true" and "fail"),
                createSolveRequest("fail" and "true"),
                createSolveRequest("true" and "true" and "fail")
        )
    }

}
