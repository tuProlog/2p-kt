package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.utils.setTags

internal class LazyConsWithImplicitLast(
    private val cursor: Cursor<out Term>,
    tags: Map<String, Any> = emptyMap(),
) : AbstractCons(emptyList(), tags),
    Cons {
    override val head: Term by lazy { cursor.current!! }

    override val tail: Term by lazy {
        cursor.next.let {
            when {
                it.isOver -> EmptyList.instance
                it.next.isOver -> it.current!!
                else -> LazyConsWithImplicitLast(it)
            }
        }
    }

    override val last: Term by lazy { super.last }

    override val args: List<Term> = LazyTwoItemsList({ head }, { tail })

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(cursor.map { it.apply(unifier) }, tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithImplicitLast =
        if (this.tags === tags) this else LazyConsWithImplicitLast(cursor, tags)

    override fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = unifier.isEmpty()

    override fun freshCopy(scope: Scope): Cons =
        LazyConsWithImplicitLast(unfoldedSequence.map { it.freshCopy(scope) }.cursor()).setTags(tags).castToCons()
}
