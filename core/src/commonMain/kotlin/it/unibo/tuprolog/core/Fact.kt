package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Fact : Rule {
    override val body: Term
        get() = Truth.TRUE

    override val isFact: Boolean
        get() = true

    override fun freshCopy(): Fact

    override fun freshCopy(scope: Scope): Fact

    override fun asFact(): Fact = this

    override fun setHeadFunctor(functor: String): Fact

    override fun setHeadArgs(vararg arguments: Term): Fact

    override fun setHeadArgs(arguments: Iterable<Term>): Fact

    override fun setHeadArgs(arguments: Sequence<Term>): Fact

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Fact

    override fun addFirstHeadArg(argument: Term): Fact

    override fun addLastHeadArg(argument: Term): Fact

    override fun appendHeadArg(argument: Term): Fact

    companion object {
        const val FUNCTOR = Terms.CLAUSE_FUNCTOR

        @JvmStatic
        @JsName("ofStruct")
        fun of(head: Struct): Fact = TermFactory.default.factOf(head)

        @JsName("of")
        @JvmStatic
        fun of(
            functor: String,
            vararg args: Term,
        ): Fact = TermFactory.default.factOf(functor, *args)

        @JsName("ofIterable")
        @JvmStatic
        fun of(
            functor: String,
            args: Iterable<Term>,
        ): Fact = TermFactory.default.factOf(functor, args)

        @JsName("ofSequence")
        @JvmStatic
        fun of(
            functor: String,
            args: Sequence<Term>,
        ): Fact = TermFactory.default.factOf(functor, args)

        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Fact = TermFactory.default.factTemplateOf(functor, arity)
    }
}
