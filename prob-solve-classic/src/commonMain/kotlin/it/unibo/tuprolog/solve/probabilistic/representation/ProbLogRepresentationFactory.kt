package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

internal object ProbLogRepresentationFactory: ProbabilisticRepresentationFactory {
    override val defaultBuiltins: AliasedLibrary
        get() = ProbLogLibrary

    override fun fromPrologTerm(term: Term, probability: Term): ProbabilisticTerm {
        return ProbLogTerm(term, probability)
    }

    override fun fromPrologClause(clause: Clause, probability: Term): ProbabilisticClause {
        return ProbLogClause(clause, probability)
    }

    override fun fromPrologTheory(theory: Theory): ProbabilisticTheory {
        return ProbLogTheory(theory)
    }
}

@JsName("problogFactory")
fun ProbabilisticRepresentationFactory.Companion.ofProbLog(): ProbabilisticRepresentationFactory {
    return ProbLogRepresentationFactory
}