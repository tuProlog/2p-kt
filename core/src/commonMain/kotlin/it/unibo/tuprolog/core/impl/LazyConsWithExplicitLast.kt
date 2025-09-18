package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.utils.dropLast
import it.unibo.tuprolog.utils.setTags

internal class LazyConsWithExplicitLast(
    private val cursor: Cursor<out Term>,
    private val termination: Term = EmptyList.instance,
    tags: Map<String, Any> = emptyMap(),
) : AbstractCons(emptyList(), tags),
    Cons {
    override val head: Term
        get() = cursor.current!!

    override val tail: Term by lazy {
        cursor.next.let {
            if (it.isOver) {
                termination
            } else {
                LazyConsWithExplicitLast(it, termination)
            }
        }
    }

    override val last: Term by lazy {
        var current: Term = this
        while (current.isCons) {
            if (current is LazyConsWithExplicitLast) {
                current = current.termination
            } else if (current.isList) {
                current = current.castToList().last
            }
        }
        current
    }

    override val args: List<Term> = LazyTwoItemsList({ head }, { tail })

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(unfoldedSequence.map { it.apply(unifier) }.cursor(), tags)

    override fun copyWithTags(tags: Map<String, Any>): LazyConsWithExplicitLast =
        if (this.tags === tags) this else LazyConsWithExplicitLast(cursor, termination, tags)

    override fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = unifier.isEmpty()

    override fun freshCopy(scope: Scope): Cons =
        LazyConsWithExplicitLast(
            unfoldedSequence.dropLast().map { it.freshCopy(scope) }.cursor(),
            last.freshCopy(scope),
        ).setTags(tags).castToCons()
}
