package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl

interface Fact : Rule {

    override val body: Term
        get() = Truth.`true`()

    override val isFact: Boolean
        get() = true

    override fun freshCopy(): Fact = super.freshCopy() as Fact

    override fun freshCopy(scope: Scope): Fact = super.freshCopy(scope) as Fact

    override fun <T> accept(visitor: TermVisitor<T>): T =
            visitor.visit(this)

    companion object {
        fun of(head: Struct): Fact = FactImpl(head)
    }
}
