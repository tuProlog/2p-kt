package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.CONS_FUNCTOR
import it.unibo.tuprolog.utils.setTags
import it.unibo.tuprolog.core.ListIterator as LogicListIterator

internal abstract class AbstractCons(
    args: List<Term>,
    tags: Map<String, Any> = emptyMap(),
) : RecursiveImpl(CONS_FUNCTOR, args, tags),
    Cons {
    companion object {
        const val SWITCH_TO_LAZY_THRESHOLD = 100
    }

    override val estimatedLength: Int
        get() = 1

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { LogicListIterator.All(this) }.asSequence()

    override val functor: String = CONS_FUNCTOR

    private val lastCache: Lazy<Term> = lazy { unfoldedSequence.last() }

    override val isWellFormed: Boolean
        get() = last.isEmptyList

    /** This list's [last] term, if it is already known without traversing any lazy suffix, or `null`. */
    internal open val knownLast: Term?
        get() = lastCache.valueIfInitialized()

    override val size: Int
        get() = super<RecursiveImpl>.size

    override fun computeSize(): Int {
        var count = 0
        var last: Term? = null
        for (item in unfoldedSequence) {
            count++
            last = item
        }
        return if (last?.isEmptyList == true) count - 1 else count
    }

    override fun unfold(): Sequence<Term> = Iterable { ListUnfolder(this) }.asSequence()

    override fun toArray(): Array<Term> =
        when {
            isWellFormed -> unfoldedArray.sliceArray(0 until unfoldedArray.lastIndex)
            else -> unfoldedArray
        }

    override fun toList(): List<Term> =
        when {
            isWellFormed -> unfoldedList.subList(0, unfoldedList.lastIndex)
            else -> unfoldedList
        }

    override fun toSequence(): Sequence<Term> = LogicListIterator.SkippingLast(this).asSequence()

    override val last: Term
        get() = lastCache.value

    override fun toString(): String {
        val (ending, take) =
            if (isWellFormed) {
                "]" to size
            } else {
                " | $last]" to size - 1
            }
        return unfoldedSequence.take(take).joinToString(", ", "[", ending)
    }

    abstract override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term

    abstract override fun copyWithTags(tags: Map<String, Any>): AbstractCons

    override fun freshCopy(): Cons = super.freshCopy().castToCons()

    override fun freshCopy(scope: Scope): Cons = scope.logicListFrom(unfoldedSequence).setTags(tags).castToCons()

    final override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitCons(this)
}
