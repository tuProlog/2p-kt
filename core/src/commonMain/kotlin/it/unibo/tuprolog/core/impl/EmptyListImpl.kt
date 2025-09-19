package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.EMPTY_LIST_FUNCTOR

internal class EmptyListImpl(
    tags: Map<String, Any> = emptyMap(),
) : AtomImpl(EMPTY_LIST_FUNCTOR, tags),
    EmptyList {
    override val unfoldedList: List<Term> = listOf(this)

    override val unfoldedSequence: Sequence<Term> = sequenceOf(this)

    override val unfoldedArray: Array<Term> = arrayOf(this)

    override val items: Iterable<Term> = emptyList()

    override fun unfold(): Sequence<Term> = sequenceOf(this)

    override fun toString(): String = value

    override val last: Term
        get() = this

    override val estimatedLength: Int
        get() = 0

    override val size: Int
        get() = 0

    override val isWellFormed: Boolean
        get() = true

    override fun copyWithTags(tags: Map<String, Any>): EmptyList = EmptyListImpl(tags)

    override fun freshCopy(): EmptyList = this

    override fun freshCopy(scope: Scope): EmptyList = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitEmptyList(this)
}
