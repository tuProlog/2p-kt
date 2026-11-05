package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.setTags

/**
 * A right-nested tuple whose items are lazily pulled out of [cursor], which must yield at least 2 items:
 * [left] is the cursor's current item, and [right] is either its last item or another [LazyTupleImpl].
 */
internal class LazyTupleImpl(
    private val cursor: Cursor<out Term>,
    tags: Map<String, Any> = emptyMap(),
) : AbstractTuple(emptyList(), tags) {
    override val left: Term
        get() = cursor.current!!

    private var cachedRight: Term? = null

    override val right: Term
        get() =
            cachedRight ?: cursor.next
                .let { if (it.next.isOver) it.current!! else LazyTupleImpl(it) }
                .also { cachedRight = it }

    override val firstChild: Term
        get() = left

    override val secondChild: Term
        get() = right

    override val args: List<Term> = LazyArgs()

    override fun copyWithTags(tags: Map<String, Any>): Tuple =
        if (this.tags === tags) this else LazyTupleImpl(cursor, tags)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyTupleImpl(cursor.map { it.apply(unifier) }, tags)

    override fun freshCopy(scope: Scope): Tuple = LazyTupleImpl(cursor.map { it.freshCopy(scope) }).setTags(tags)
}
