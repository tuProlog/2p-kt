package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor

internal open class LazyConsWithImplicitLast(
    private val cursor: Cursor<out Term>,
    tags: Map<String, Any> = emptyMap()
) : AbstractCons(emptyArray(), tags), Cons {

    override val head: Term by lazy { cursor.current!! }

    override val tail: Term
        get() = cursor.next.let {
            when {
                it.isOver -> EmptyList.instance
                it.next.isOver -> it.current!!
                else -> LazyConsWithImplicitLast(it)
            }
        }

    override val args: Array<Term> get() = arrayOf(head, tail)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(cursor.map { it.apply(unifier) }, tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithImplicitLast =
        if (this.tags === tags) this else LazyConsWithImplicitLast(cursor, tags)
}
