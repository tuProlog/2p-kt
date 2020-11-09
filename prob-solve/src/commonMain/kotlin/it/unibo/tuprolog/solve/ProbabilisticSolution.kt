package it.unibo.tuprolog.solve

import kotlin.js.JsName

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