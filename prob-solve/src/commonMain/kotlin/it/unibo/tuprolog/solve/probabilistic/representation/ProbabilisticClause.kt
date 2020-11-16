package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

/**
 * Represents a Clause along with its probability of being true.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticClause: Clause {

    /* Makes the interface open to extension methods */
    companion object

    @JsName("toPrologClause")
    fun toPrologClause(): Clause

    @JsName("toProbability")
    fun toProbability(): Term
}