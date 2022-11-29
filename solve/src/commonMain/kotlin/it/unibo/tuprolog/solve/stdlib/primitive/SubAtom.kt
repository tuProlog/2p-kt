package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

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
                        yield(mgu(second, Integer.ZERO))
                        yield(mgu(third, Integer.ZERO))
                        yield(mgu(fourth, Integer.of((first as Atom).value.length)))
                        yield(mgu(fifth, Atom.of("")))
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        if (third is Var) {
                            val result = (first as Atom).value.substring(0, (fourth as Integer).intValue.toInt())
                            yield(mgu(fifth, Atom.of(result)))
                            yield(mgu(second, Integer.ZERO))
                        } else {
                            ensuringArgumentIsInteger(2)
                            val before = (fourth as Integer).intValue.toInt() + (third as Integer).intValue.toInt()
                            val result = (first as Atom).value.substring(before, fourth.intValue.toInt())
                            yield(mgu(fifth, Atom.of(result)))
                            yield(mgu(second, Integer.of(before)))
                        }
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVar) {
                    sequence<Substitution> {
                        if (third is Var) {
                            val result = (first as Atom).value.substring((second as Integer).intValue.toInt())
                            yield(mgu(fifth, Atom.of(result)))
                            yield(mgu(Integer.of(result.length), third))
                            yield(mgu(fourth, Integer.of(first.value.length - second.intValue.toInt())))
                        } else {
                            ensuringArgumentIsInteger(2)
                            val before = (second as Integer).intValue.toInt()
                            val result =
                                (first as Atom).value.substring(before, before + (third as Integer).intValue.toInt())
                            yield(mgu(fifth, Atom.of(result)))
                            yield(mgu(fourth, Integer.of(first.value.length - result.length - before)))
                        }
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    ensuringArgumentIsInteger(1)
                    sequence<Substitution> {
                        val subEnd = (first as Atom).value.length - (fourth as Integer).intValue.toInt()
                        val result = first.value.substring((second as Integer).intValue.toInt(), subEnd)
                        yield(mgu(fifth, Atom.of(result)))
                        if (third is Var) {
                            yield(mgu(Integer.of(result.length), third))
                        }
                    }
                }
            }
        } else {
            ensuringArgumentIsAtom(0)
            ensuringArgumentIsAtom(4)
            if (second.isVar) {
                if (fourth.isVar) {
                    sequence {
                        val result = (fifth as Atom).value
                        val before = (first as Atom).value.substringBefore(result, "").length
                        val after = first.value.substringAfter(result, "").length
                        yield(mgu(second, Integer.of(before)))
                        yield(mgu(fourth, Integer.of(after)))
                        if (third is Var) {
                            yield(mgu(third, Integer.of(result.length)))
                        }
                    }
                } else {
                    sequence {
                        val result = (fifth as Atom).value
                        val before = (first as Atom).value.substringBefore(result, "").length
                        yield(mgu(second, Integer.of(before)))
                        if (third is Var) {
                            yield(mgu(third, Integer.of(result.length)))
                        }
                    }
                }
            } else {
                ensuringArgumentIsInteger(1)
                if (fourth.isVar) {
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        val after = (first as Atom).value.substringAfter(result, "").length
                        yield(mgu(fourth, Integer.of(after)))
                        if (third is Var) {
                            yield(mgu(third, Integer.of(result.length)))
                        }
                    }
                } else {
                    ensuringArgumentIsInteger(3)
                    sequence<Substitution> {
                        val result = (fifth as Atom).value
                        if (third is Var) {
                            yield(mgu(third, Integer.of(result.length)))
                        }
                    }
                }
            }
        }
    }
}
