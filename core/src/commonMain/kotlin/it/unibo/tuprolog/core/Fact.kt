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

    companion object {

        const val FUNCTOR = Terms.CLAUSE_FUNCTOR

        @JvmStatic
        @JsName("of")
        fun of(head: Struct): Fact = FactImpl(head)

        @JvmStatic
        @JsName("template")
        fun template(functor: String, arity: Int): Fact {
            return of(Struct.template(functor, arity))
        }
    }
}
