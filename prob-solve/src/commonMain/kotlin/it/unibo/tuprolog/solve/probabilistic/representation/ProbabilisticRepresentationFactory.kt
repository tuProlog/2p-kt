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

    @JsName("fromPrologTerm")
    fun fromPrologTerm(term: Term, probability: Term): ProbabilisticTerm

    @JsName("fromPrologClause")
    fun fromPrologClause(clause: Clause, probability: Term): ProbabilisticClause

    @JsName("fromPrologTheory")
    fun fromPrologTheory(theory: Theory): ProbabilisticTheory


}
