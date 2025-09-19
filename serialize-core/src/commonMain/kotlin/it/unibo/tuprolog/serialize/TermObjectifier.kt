package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermObjectifier :
    Objectifier<Term>,
    TermVisitor<Any> {
    override fun objectify(value: Term): Any = value.accept(this)

    companion object {
        @JsName("default")
        @JvmStatic
        val default: TermObjectifier
            get() = termObjectifier()
    }
}
