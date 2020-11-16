package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface ProbabilisticRepresentationFactory {

    /* Makes the interface open to extension methods */
    companion object

    @JsName("defaultBuiltins")
    val defaultBuiltins: AliasedLibrary

    @JsName("fromTermAndProbability")
    fun from(term: Term, probability: Term): ProbabilisticTerm

    @JsName("fromTerm")
    fun from(term: Term): ProbabilisticTerm

    @JsName("fromClause")
    fun from(clause: Clause): ProbabilisticClause

    @JsName("fromClauseAndProbability")
    fun from(clause: Clause, probability: Term): ProbabilisticClause

    @JsName("fromTheory")
    fun from(theory: Theory): ProbabilisticTheory


}
