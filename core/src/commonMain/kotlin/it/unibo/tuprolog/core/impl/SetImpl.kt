package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.SetIterator
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.setTags
import it.unibo.tuprolog.core.Set as LogicSet

internal open class SetImpl(
    private val item: Term?,
    tags: Map<String, Any> = emptyMap()
) : CollectionImpl(LogicSet.FUNCTOR, listOfNotNull(item).toTypedArray(), tags), LogicSet {

    override val functor: String = super<LogicSet>.functor

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { SetIterator(this) }.asSequence()

    override val size: Int get() = unfoldedList.size

    override fun unfold(): Sequence<Term> = Iterable { SetUnfolder(this) }.asSequence()

    override fun toString(): String = unfoldedSequence.joinToString(", ", "{", "}")

    override fun replaceTags(tags: Map<String, Any>): LogicSet = SetImpl(item, tags)

    override fun freshCopy(): LogicSet = super.freshCopy() as LogicSet

    override fun freshCopy(scope: Scope): LogicSet =
        when {
            isGround -> this
            else -> scope.setOf(argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }
}
