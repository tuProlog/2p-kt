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
    init {
        cachedGround = if (item == null) true else item.knownGround
        cachedSize =
            when {
                item == null -> 0
                item.isTuple -> item.knownSize
                else -> 1
            }
    }

    override val functor: String = super<Block>.functor

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { BlockIterator(this) }.asSequence()

    override fun unfold(): Sequence<Term> = Iterable { BlockUnfolder(this) }.asSequence()

    // explicit, as on Kotlin/JS the interface's default implementations are not exposed under their @JsName
    override fun toList(): List<Term> = unfoldedList

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun toString(): String = unfoldedSequence.joinToString(", ", "{", "}")

    override fun copyWithTags(tags: Map<String, Any>): Block = BlockImpl(item, tags)

    override fun freshCopy(): Block = super.freshCopy().castToBlock()

    override fun freshCopy(scope: Scope): Block =
        when {
            knownGround == true -> this
            else -> scope.blockOf(argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitBlock(this)
}
