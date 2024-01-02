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

    override fun freshCopy(): Directive

    override fun freshCopy(scope: Scope): Directive

    override fun asDirective(): Directive = this

    companion object {
        @JvmStatic
        @JsName("ofSequence")
        fun of(bodies: Sequence<Term>): Directive = of(bodies.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(bodies: Iterable<Term>): Directive {
            require(bodies.any()) { "Directive requires at least one body element" }
            return DirectiveImpl(Tuple.wrapIfNeeded(bodies))
        }

        @JvmStatic
        @JsName("of")
        fun of(
            body1: Term,
            vararg body: Term,
        ): Directive = of(listOf(body1, *body))

        @JvmStatic
        @JsName("template")
        fun template(length: Int = 1): Directive {
            require(length > 0)
            return of((0 until length).map { Var.anonymous() })
        }
    }
}
