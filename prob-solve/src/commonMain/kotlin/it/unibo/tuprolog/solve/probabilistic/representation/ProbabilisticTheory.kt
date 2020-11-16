package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

/**
 * Represents a Theory capable of representing both Prolog logical clauses and probabilistic logical clauses
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticTheory: Theory {

    /* Makes the interface open to extension methods */
    companion object

    @JsName("toPrologTheory")
    fun toPrologTheory(): Theory

}
