package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermObjectifier<T> : Objectifier<Term, T>, TermVisitor<T> {
    override fun objectify(value: Term): T = visit(value)

    companion object {
        @JsName("default")
        @JvmStatic
        val default: TermObjectifier<*>
            get() = termObjectifier()
    }
}