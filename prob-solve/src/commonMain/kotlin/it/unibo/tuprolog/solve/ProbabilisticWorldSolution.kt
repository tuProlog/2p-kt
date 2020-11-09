package it.unibo.tuprolog.solve

import kotlin.js.JsName

data class ProbabilisticWorldSolution (
        @JsName("solution")
        val solution: Solution,
        @JsName("signatures")
        val signatures: Iterable<ProbabilisticSignature>,
) {
}