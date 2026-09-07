package it.unibo.tuprolog.core.visitors

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An [AbstractTermVisitor] resolving multiple-supertype terms (see [AbstractTermVisitor]) by using the
 * *first* applicable supertype's visit result, e.g. treating an [it.unibo.tuprolog.core.Atom] primarily as a
 * [it.unibo.tuprolog.core.Struct].
 */
abstract class DefaultTermVisitor<T> : AbstractTermVisitor<T>() {
    override fun <X : Term> join(
        term: X,
        f1: (X) -> T,
        vararg fs: (X) -> T,
    ): T = sequenceOf(f1, *fs).map { it(term) }.first()

    companion object {
        /** Creates a [DefaultTermVisitor] whose [TermVisitor.defaultValue] is computed by [defaultValue]. */
        @JvmStatic
        @JsName("of")
        fun <X> of(defaultValue: (Term) -> X): DefaultTermVisitor<X> =
            object : DefaultTermVisitor<X>() {
                override fun defaultValue(term: Term): X = defaultValue(term)
            }
    }
}
