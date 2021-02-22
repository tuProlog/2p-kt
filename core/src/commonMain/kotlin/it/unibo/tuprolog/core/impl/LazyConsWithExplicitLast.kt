package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor

internal open class LazyConsWithExplicitLast(
    private val cursor: Cursor<out Term>,
    override val last: Term = EmptyList.instance,
    tags: Map<String, Any> = emptyMap()
) : AbstractCons(emptyArray(), tags), Cons {

    override val head: Term by lazy { cursor.current!! }

    override val tail: Term
        get() = cursor.next.let {
            if (it.isOver) {
                last
            } else {
                LazyConsWithExplicitLast(it, last)
            }
        }

    override val args: Array<Term> get() = arrayOf(head, tail)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithExplicitLast(cursor.map { it.apply(unifier) }, last.apply(unifier), tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithExplicitLast =
        if (this.tags === tags) this else LazyConsWithExplicitLast(cursor, last, tags)
}
