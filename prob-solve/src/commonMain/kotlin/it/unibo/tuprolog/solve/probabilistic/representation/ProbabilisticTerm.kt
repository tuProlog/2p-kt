package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.Signature
import kotlin.js.JsName

/**
 * Represents a Term along with its probability of being true.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticTerm: Term {

    /* Makes the interface open to extension methods */
    companion object

    @JsName("toPrologTerm")
    fun toPrologTerm(): Term

    @JsName("toProbability")
    fun toProbability(): Term
}
