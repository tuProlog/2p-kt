package it.unibo.tuprolog.core

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
        fun of(body: Sequence<Term>): Directive = TermFactory.default.directiveOf(body)

        @JvmStatic
        @JsName("ofIterable")
        fun of(body: Iterable<Term>): Directive = TermFactory.default.directiveOf(body)

        @JvmStatic
        @JsName("of")
        fun of(
            firstGoal: Term,
            vararg otherGoals: Term,
        ): Directive = TermFactory.default.directiveOf(firstGoal, *otherGoals)

        @JvmStatic
        @JsName("template")
        fun template(length: Int = 1): Directive = TermFactory.default.directiveTemplate(length)
    }
}
