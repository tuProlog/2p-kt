package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.toTerm
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer

object SubAtom : QuinaryRelation.WithoutSideEffects<ExecutionContext>("sub_atom") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term, // string
        second: Term, // before
        third: Term, // length
        fourth: Term, // after
        fifth: Term // sub
    ): Sequence<Substitution> {
        return if (fifth is Var) {
            ensuringArgumentIsAtom(0)
            if (second is Var) {
                if (fourth is Var) {
                    sequence<Substitution> {
                        yield(second mguWith 0.toTerm())
                        yield(third mguWith "".length.toTerm())
                        yield(fourth mguWith (first as Atom).value.length.toTerm())
                        yield(fifth mguWith "".toTerm())
                    }
                } else { // before and sub are var, after is defined
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        val sub = (first as Atom).toString().substring(0, (fourth as Integer).intValue.toInt())
                        yield(second mguWith Integer.of(0))
                        yield(third mguWith sub.length.toTerm())
                        yield(fifth mguWith sub.toTerm())
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) { // before is defined, sub and after are var
                    sequence<Substitution> {
                        yield(third mguWith "".length.toTerm())
                        yield(fourth mguWith (first as Atom).value.length.minus(second as Int).toTerm())
                        yield(fifth mguWith Atom.of("")) // deve cambiare
                    }
                } else {
                    ensuringArgumentIsInteger(3) // before and after are defined, sub is var
                    sequence<Substitution> {
                        val result = (first as Atom).toString().substring((second as Int), (fourth as Int))
                        yield(third mguWith result.length.toTerm())
                        yield(fifth mguWith result.toTerm())
                    }
                }
            }
        } else {
            ensuringArgumentIsAtom(0)
            ensuringArgumentIsAtom(4)
            if (second.isVariable) {
                if (fourth.isVariable) { // before and after are var, sub is defined
                    sequence<Substitution> {
                        val sub = (fifth as Atom).toString()
                        yield(second mguWith (first as Atom).toString().substringBefore(sub, "").length.toTerm())
                        yield(fourth mguWith (first as Atom).toString().substringAfter(sub, "").length.toTerm())
                        yield(third mguWith sub.length.toTerm())
                    }
                } else { // sub and after are defined, before is var
                    sequence<Substitution> {
                        val sub = (fifth as Atom).toString()
                        yield(second mguWith (first as Atom).toString().substringBefore(sub, "").length.toTerm())
                        yield(third mguWith sub.length.toTerm())
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) { // sub and before are defined, fourth is variable
                    sequence<Substitution> {
                        val sub = (fifth as Atom).toString()
                        yield(fourth mguWith (first as Atom).toString().substringAfter(sub, "").length.toTerm())
                        yield(third mguWith sub.length.toTerm())
                    }
                } else {
                    ensuringArgumentIsInteger(3) // all are defined
                    sequence<Substitution> {
                        val sub = (fifth as Atom).toString()
                        // yield(fourth mguWith (first as Atom).toString().substringAfter(sub,"").length.toTerm())
                        yield(third mguWith sub.length.toTerm())
                    }
                }
            }
        }
    }

   /* private data class SubString(val before: Int, val after: Int, val length:Int, val sub: String)
    private fun String.substrings(sub:String): Sequence<SubString> = sequence {
        if(sub.isEmpty()){
            Sequence {SubString(bef,af,len,sub).}
        }
        else{

        }
    }*/
}
