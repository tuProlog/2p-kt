package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DirectiveImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Directive : Clause {

    override val head: Struct?
        get() = null

    override val isRule: Boolean
        get() = false

    override val isFact: Boolean
        get() = false

    override val isDirective: Boolean
        get() = true

    override fun freshCopy(): Directive = super.freshCopy() as Directive

    override fun freshCopy(scope: Scope): Directive = super.freshCopy(scope) as Directive

    companion object {
        @JvmStatic
        @JsName("ofIterable")
        fun of(bodies: Iterable<Term>): Directive {
            require(bodies.any()) { "Directive requires at least one body element" }
            return DirectiveImpl(Tuple.wrapIfNeeded(*bodies.toList().toTypedArray()))
        }

        @JvmStatic
        @JsName("of")
        fun of(body1: Term, vararg body: Term): Directive =
            of(listOf(body1, *body))
    }
}
