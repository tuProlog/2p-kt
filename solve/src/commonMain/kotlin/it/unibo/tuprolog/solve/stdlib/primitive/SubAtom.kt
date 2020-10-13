package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.toTerm
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object SubAtom : QuinaryRelation.WithoutSideEffects<ExecutionContext>("repeat") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term, // string
        second: Term, // before
        third: Term, // length
        fourth: Term, // after
        fifth: Term // sub
    ): Sequence<Substitution> {
        if (fifth.isVariable) {
            ensuringArgumentIsAtom(0)
            if (second.isVariable) { //all are var
                if (fourth.isVariable) {
                    sequence<Substitution> {
                        (second mguWith 0.toTerm())
                        (third mguWith "".length.toTerm())
                        (fourth mguWith (first as Atom).toString().length.toTerm())
                        (fifth mguWith "".toTerm())
                    }
                } else { //before and sub are var, after is defined
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        val sub = (first as Atom).toString().substring(0, fourth as Int)
                        (second mguWith 0.toTerm())
                        (third mguWith sub.length.toTerm())
                        (fifth mguWith sub.toTerm())
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) { //before is defined, sub and after are var
                    sequence<Substitution> {
                        (third mguWith "".length.toTerm())
                        (fourth mguWith (first as Atom).toString().length.minus(second as Int).toTerm())
                        (fifth mguWith "".toTerm()) //deve cambiare
                    }
                } else {
                    ensuringArgumentIsInteger(3) //before and after are defined, sub is var
                    sequence<Substitution> {
                        val result=(first as Atom).toString().substring((second as Int),(fourth as Int))
                        (third mguWith result.length.toTerm())
                        (fifth mguWith result.toTerm())
                    }
                }
            }
        } else {
            ensuringArgumentIsAtom(0)
            if (second.isVariable) {
                if (fourth.isVariable) { //before and after are var, sub is defined
                } else {
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) {
                } else {
                    ensuringArgumentIsInteger(3)
                }
            }
        }

        TODO("Not yet implemented")
    }

    // private data class SubString(val start: Int, val end: Int, val value: String)
    // private fun String.substrings(): Sequence<SubString> = sequence {
    // }
}
