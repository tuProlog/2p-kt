package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RuleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Rule : Clause {

    override val head: Struct

    override val isRule: Boolean
        get() = true

    override val isFact: Boolean
        get() = body.isTrue

    override val isDirective: Boolean
        get() = false

    override fun freshCopy(): Rule = super.freshCopy() as Rule

    override fun freshCopy(scope: Scope): Rule = super.freshCopy(scope) as Rule

    override fun tag(name: String, value: Any): Rule

    companion object {

        const val FUNCTOR = ":-"

        @JvmStatic
        @JsName("of")
        fun of(head: Struct, vararg body: Term): Rule =
            when {
                body.isEmpty() || body.size == 1 && body[0].isTrue -> Fact.of(head)
                else -> RuleImpl(head, Tuple.wrapIfNeeded(*body))
            }

        @JvmStatic
        @JsName("template")
        fun template(functor: String, arity: Int): Rule {
            return of(Struct.template(functor, arity), Var.anonymous())
        }
    }
}
