package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth

internal class FactImpl(
    override val head: Struct,
    tags: Map<String, Any> = emptyMap()
) : RuleImpl(head, Truth.TRUE, tags), Fact {

    override val isWellFormed: Boolean = true

    override val body: Term = super<RuleImpl>.body

    override fun replaceTags(tags: Map<String, Any>): Fact = FactImpl(head, tags)

    override fun freshCopy(): Fact = super.freshCopy() as Fact

    override fun freshCopy(scope: Scope): Fact = super.freshCopy(scope) as Fact
}
