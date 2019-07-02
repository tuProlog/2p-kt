package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DirectiveImpl

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
        fun of(body1: Term, vararg body: Term): Directive =
                DirectiveImpl(Struct.conjunction((listOf(body1) + listOf(*body))))
    }
}
