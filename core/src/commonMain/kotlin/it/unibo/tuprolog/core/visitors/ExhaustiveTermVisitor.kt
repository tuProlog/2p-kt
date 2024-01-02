package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

abstract class ExhaustiveTermVisitor<T> : AbstractTermVisitor<T>() {
    override fun <X : Term> join(
        term: X,
        f1: (X) -> T,
        vararg fs: (X) -> T,
    ): T = sequenceOf(f1, *fs).map { it(term) }.last()

    companion object {
        @JvmStatic
        @JsName("of")
        fun <X> of(defaultValue: (Term) -> X): ExhaustiveTermVisitor<X> =
            object : ExhaustiveTermVisitor<X>() {
                override fun defaultValue(term: Term): X = defaultValue(term)
            }
    }
}
