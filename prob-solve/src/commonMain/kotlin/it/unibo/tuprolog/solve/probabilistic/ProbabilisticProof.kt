package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

/**
 * Represents a single proof for a given probabilistic query (also known as "Probabilistic World").
 *
 * @author Jason Dellaluce
 */
data class ProbabilisticProof (
        @JsName("solution")
        val solution: Solution,
        @JsName("proofSignatures")
        val proofSignatures: Iterable<ProbabilisticSignature>,
) {
    val probability: Double
        get() = proofSignatures.map { s -> s.probability }.reduce(Double::times)
}