package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.BlockIterator
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.BLOCK_FUNCTOR
import it.unibo.tuprolog.utils.setTags

internal open class BlockImpl(
    private val item: Term?,
    tags: Map<String, Any> = emptyMap(),
) : RecursiveImpl(BLOCK_FUNCTOR, listOfNotNull(item), tags),
    Block {
    override val isGround: Boolean = checkGroundness()

    override fun checkGroundness(): Boolean = item?.isGround ?: true

    override val functor: String = super<Block>.functor

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { BlockIterator(this) }.asSequence()

    override val size: Int get() = unfoldedList.size

    override fun unfold(): Sequence<Term> = Iterable { BlockUnfolder(this) }.asSequence()

    override fun toString(): String = unfoldedSequence.joinToString(", ", "{", "}")

    override fun copyWithTags(tags: Map<String, Any>): Block = BlockImpl(item, tags)

    override fun freshCopy(): Block = super.freshCopy().castToBlock()

    override fun freshCopy(scope: Scope): Block =
        when {
            isGround -> this
            else -> scope.blockOf(argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitBlock(this)
}
