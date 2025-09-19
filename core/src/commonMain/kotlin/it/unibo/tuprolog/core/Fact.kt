package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl
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
        @JsName("of")
        fun of(head: Struct): Fact = FactImpl(head)

        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Fact = of(Struct.template(functor, arity))
    }
}
