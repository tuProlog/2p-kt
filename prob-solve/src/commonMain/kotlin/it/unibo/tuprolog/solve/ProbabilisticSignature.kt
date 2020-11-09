package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import kotlin.js.JsName

data class ProbabilisticSignature(
    @JsName("signature")
    val signature: Signature,
    @JsName("probability")
    val probability: Double,
) : ToTermConvertible {

    /* TODO: Do we want to encode probability in here?
        Also do we really need to implement ToTermConvertible? */
    override fun toTerm(): Term {
        return signature.toTerm()
    }

    init {
        require(probability in 0.0..1.0) {
            "ProbabilisticSignature probability should be a value between 0 and 1: $probability"
        }
    }

}
