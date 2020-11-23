package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object SubAtom : QuinaryRelation.WithoutSideEffects<ExecutionContext>("sub_atom") {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term, // string
        second: Term, // before
        third: Term, // length
        fourth: Term, // after
        fifth: Term // sub
    ): Sequence<Substitution> {
        return if (fifth is Var) {
            ensuringArgumentIsInstantiated(0)
            ensuringArgumentIsAtom(0)
            if (second is Var) {
                if (fourth is Var) {
                    sequence<Substitution> {
                        yield(second mguWith Integer.ZERO)
                        yield(third mguWith Integer.of("".length))
                        yield(fourth mguWith Integer.of((first as Atom).value.length))
                        yield(fifth mguWith Atom.of(""))
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        if (third is Var) {
                            val result = (first as Atom).value.substring(0, (fourth as Integer).intValue.toInt())
                            yield(fifth mguWith Atom.of(result))
                            yield(second mguWith Integer.ZERO)
                        } else {
                            ensuringArgumentIsInteger(2)
                            val before = (fourth as Integer).intValue.toInt() + (third as Integer).intValue.toInt()
                            val result = (first as Atom).value.substring(before, fourth.intValue.toInt())
                            yield(fifth mguWith Atom.of(result))
                            yield(second mguWith Integer.of(before))
                        }
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) {
                    sequence<Substitution> {
                        if (third is Var) {
                            val result = (first as Atom).value.substring((second as Integer).intValue.toInt())
                            yield(fifth mguWith Atom.of(result))
                            yield(Integer.of(result.length) mguWith third)
                            yield(fourth mguWith Integer.of(first.value.length - second.intValue.toInt()))
                        } else {
                            ensuringArgumentIsInteger(2)
                            val before = (second as Integer).intValue.toInt()
                            val result =
                                (first as Atom).value.substring(before, before + (third as Integer).intValue.toInt())
                            yield(fifth mguWith Atom.of(result))
                            yield(fourth mguWith Integer.of(first.value.length - result.length - before))
                        }
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    ensuringArgumentIsInteger(1)
                    sequence<Substitution> {
                        val subEnd = (first as Atom).value.length - (fourth as Integer).intValue.toInt()
                        val result = first.value.substring((second as Integer).intValue.toInt(), subEnd)
                        yield(fifth mguWith Atom.of(result))
                        if (third is Var) {
                            yield(Integer.of(result.length) mguWith third)
                        }
                    }
                }
            }
        } else {
            ensuringArgumentIsAtom(0)
            ensuringArgumentIsAtom(4)
            if (second.isVariable) {
                if (fourth.isVariable) {
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        val before = (first as Atom).value.substringBefore(result, "").length
                        val after = first.value.substringAfter(result, "").length
                        yield(second mguWith Integer.of(before))
                        yield(fourth mguWith Integer.of(after))
                        if (third is Var) {
                            yield(third mguWith Integer.of(result.length))
                        }
                    }
                } else {
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        val before = (first as Atom).value.substringBefore(result, "").length
                        yield(second mguWith Integer.of(before))
                        if (third is Var) {
                            yield(third mguWith Integer.of(result.length))
                        }
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVariable) {
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        val after = (first as Atom).value.substringAfter(result, "").length
                        yield(fourth mguWith Integer.of(after))
                        if (third is Var) {
                            yield(third mguWith Integer.of(result.length))
                        }
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        if (third is Var) {
                            yield(third mguWith Integer.of(result.length))
                        }
                    }
                }
            }
        }
    }
}
