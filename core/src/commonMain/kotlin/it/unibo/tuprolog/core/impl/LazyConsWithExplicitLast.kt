package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor

internal open class LazyConsWithExplicitLast(
    private val cursor: Cursor<out Term>,
    private val termination: Term = EmptyList.instance,
    tags: Map<String, Any> = emptyMap()
) : AbstractCons(emptyArray(), tags), Cons {

    override val head: Term by lazy { cursor.current!! }

    override val tail: Term
        get() = cursor.next.let {
            if (it.isOver) {
                termination
            } else {
                LazyConsWithExplicitLast(it, termination)
            }
        }

    override val args: Array<Term> get() = arrayOf(head, tail)

    override val last: Term by lazy {
        var current: Term = this
        while (current.isCons) {
            if (current is LazyConsWithExplicitLast) {
                current = current.termination
            } else if (current is List) {
                current = current.last
            }
        }
        current
    }

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(unfoldedSequence.map { it.apply(unifier) }.cursor(), tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithExplicitLast =
        if (this.tags === tags) this else LazyConsWithExplicitLast(cursor, termination, tags)
}
