package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

internal class LazyConsWithImplicitLast(
    private val cursor: Cursor<out Term>,
    tags: Map<String, Any> = emptyMap(),
) : AbstractCons(emptyList(), tags),
    Cons {
    override val head: Term
        get() = cursor.current!!

    private var cachedTail: Term? = null

    override val tail: Term
        get() =
            cachedTail ?: cursor.next
                .let {
                    when {
                        it.isOver -> EmptyList.instance
                        it.next.isOver -> it.current!!
                        else -> LazyConsWithImplicitLast(it)
                    }
                }.also { cachedTail = it }

    override val firstChild: Term
        get() = head

    override val secondChild: Term
        get() = tail

    override val args: List<Term> = LazyArgs()

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(cursor.map { it.apply(unifier) }, tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithImplicitLast =
        if (this.tags === tags) this else LazyConsWithImplicitLast(cursor, tags)

    override fun freshCopy(scope: Scope): Cons = LazyConsWithImplicitLast(cursor.map { it.freshCopy(scope) }, tags)
}
