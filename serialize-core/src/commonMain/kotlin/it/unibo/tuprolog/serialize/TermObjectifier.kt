package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.objectify.Objectifier
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermObjectifier : Objectifier<Term>, TermVisitor<Any> {
    override fun objectify(value: Term): Any = visit(value)

    companion object {
        @JsName("default")
        @JvmStatic
        val default: TermObjectifier
            get() = termObjectifier()
    }
}