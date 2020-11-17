package it.unibo.tuprolog.solve.probabilistic.representation

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

internal object ProbLogRepresentationFactory: ProbabilisticRepresentationFactory {
    override val defaultBuiltins: AliasedLibrary
        get() = ProbLogLibrary

    override fun from(term: Term): ProbabilisticTerm {
        return from(term, Var.of("PROB"))
    }

    override fun from(clause: Clause): ProbabilisticClause {
        return from(clause, Var.of("PROB"))
    }

    override fun from(term: Term, probability: Term): ProbabilisticTerm {
        return when(term) {
            is Truth -> ProbLogTruth(term)
            else -> ProbLogTerm(term, probability)
        }
    }

    override fun from(clause: Clause, probability: Term): ProbabilisticClause {
        return when(clause) {
            is Rule -> when(clause) {
                is Fact -> ProbLogFact(clause, probability)
                else -> ProbLogRule(clause, probability)
            }
            is Directive -> ProbLogDirective(clause, probability)
            else -> ProbLogClause(clause, probability)
        }
    }

    override fun from(theory: Theory): ProbabilisticTheory {
        return ProbLogTheory(theory)
    }
}

@JsName("problogFactory")
fun ProbabilisticRepresentationFactory.Companion.ofProbLog(): ProbabilisticRepresentationFactory {
    return ProbLogRepresentationFactory
}