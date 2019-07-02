package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RuleImpl

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

    companion object {
        const val FUNCTOR = ":-"

        fun of(head: Struct, vararg body: Term): Rule =
                if (body.isEmpty() || (body.size == 1 && body[0].isTrue)) {
                    Fact.of(head)
                } else {
                    RuleImpl(head, Struct.conjunction(listOf(*body)))
                }
    }
}