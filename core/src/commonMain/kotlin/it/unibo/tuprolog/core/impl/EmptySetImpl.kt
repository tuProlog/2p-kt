package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var

internal class EmptySetImpl(
    tags: Map<String, Any> = emptyMap()
) : SetImpl(null, tags), EmptySet {

    override val args: List<Term> get() = emptyList()

    override val functor: String = super<EmptySet>.functor

    override val isGround: Boolean get() = super<SetImpl>.isGround

    override val size: Int get() = 0

    override val variables: Sequence<Var> = emptySequence()

    override fun copyWithTags(tags: Map<String, Any>): EmptySet = EmptySetImpl(tags)

    override fun freshCopy(): EmptySet = this

    override fun freshCopy(scope: Scope): EmptySet = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitEmptySet(this)
}
