package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

/**
 * Represents a solution for a given probabilistic query calculated over all possible probabilistic worlds.
 *
 * @author Jason Dellaluce
 */
data class ProbabilisticSolution (
        @JsName("solution")
    val solution: Solution,
        @JsName("probability")
    val probability: Double,
) {
    init {
        require(probability in 0.0..1.0) {
            "ProbabilisticSolution probability should be a value between 0 and 1: $probability"
        }
    }
}