package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var

internal class EmptyBlockImpl(
    tags: Map<String, Any> = emptyMap(),
) : BlockImpl(null, tags),
    EmptyBlock {
    override val args: List<Term> get() = emptyList()

    override val functor: String = super<EmptyBlock>.functor

    override val isGround: Boolean get() = true

    override val size: Int get() = 0

    override val variables: Sequence<Var> = emptySequence()

    override fun copyWithTags(tags: Map<String, Any>): EmptyBlock = EmptyBlockImpl(tags)

    override fun freshCopy(): EmptyBlock = this

    override fun freshCopy(scope: Scope): EmptyBlock = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitEmptyBlock(this)
}
