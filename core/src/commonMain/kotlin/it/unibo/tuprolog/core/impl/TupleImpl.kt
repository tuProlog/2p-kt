package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.TupleIterator
import it.unibo.tuprolog.utils.setTags

internal class TupleImpl(
    override val left: Term,
    override val right: Term,
    tags: Map<String, Any> = emptyMap()
) : CollectionImpl(Tuple.FUNCTOR, arrayOf(left, right), tags), Tuple {

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { TupleIterator(this) }.asSequence()

    override val size: Int get() = unfoldedList.size

    override fun unfold(): Sequence<Term> = Iterable { TupleUnfolder(this) }.asSequence()

    override val functor: String = Tuple.FUNCTOR

    override val args: Array<Term> get() = super<CollectionImpl>.args

    override fun toString(): String = unfoldedSequence.joinToString(", ", "(", ")")

    override fun replaceTags(tags: Map<String, Any>): Tuple = TupleImpl(left, right, tags)

    override fun freshCopy(): Tuple = super.freshCopy() as Tuple

    override fun freshCopy(scope: Scope): Tuple =
        when {
            isGround -> this
            else -> scope.tupleOf(argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }
}
