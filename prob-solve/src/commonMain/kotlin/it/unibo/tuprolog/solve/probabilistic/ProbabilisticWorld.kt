package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.probabilistic.representation.ProbabilisticTerm
import kotlin.js.JsName

/**
 * Represents a single proof for a given probabilistic query (also known as "Probabilistic World").
 *
 * @author Jason Dellaluce
 */
data class ProbabilisticWorld (
        @JsName("solution")
        val solution: Solution,
        @JsName("proofConjunction")
        val proofConjunction: Iterable<ProbabilisticTerm>,
)