package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

internal class LazyConsWithExplicitLast(
    private val cursor: Cursor<out Term>,
    private val termination: Term = EmptyList.instance,
    tags: Map<String, Any> = emptyMap(),
) : AbstractCons(emptyList(), tags),
    Cons {
    override val head: Term
        get() = cursor.current!!

    private var cachedTail: Term? = null

    override val tail: Term
        get() =
            cachedTail ?: cursor.next
                .let { if (it.isOver) termination else LazyConsWithExplicitLast(it, termination) }
                .also { cachedTail = it }

    override val firstChild: Term
        get() = head

    override val secondChild: Term
        get() = tail

    override fun computeLast(): Term {
        var current: Term = this
        while (current.isCons) {
            if (current is LazyConsWithExplicitLast) {
                current = current.termination
            } else if (current.isList) {
                current = current.castToList().last
            }
        }
        return current
    }

    override val args: List<Term> = LazyArgs()

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithExplicitLast(cursor.map { it.apply(unifier) }, termination.apply(unifier), tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithExplicitLast =
        if (this.tags === tags) this else LazyConsWithExplicitLast(cursor, termination, tags)

    override fun freshCopy(scope: Scope): Cons =
        LazyConsWithExplicitLast(cursor.map { it.freshCopy(scope) }, termination.freshCopy(scope), tags)
}
