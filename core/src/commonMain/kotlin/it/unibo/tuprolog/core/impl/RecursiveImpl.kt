package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Recursive
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.itemWiseEquals
import it.unibo.tuprolog.utils.itemWiseHashCode
import kotlin.collections.Collection as KtCollection
import kotlin.collections.List as KtList

internal abstract class RecursiveImpl(
    functor: String,
    args: KtList<Term>,
    tags: Map<String, Any>,
) : AbstractStruct(functor, args, tags),
    Recursive {
    override val unfoldedList: KtList<Term> by lazy { dequeOf(unfoldedSequence) }

    override val unfoldedArray: Array<Term>
        get() = unfoldedList.toTypedArray()

    override val isGround: Boolean
        get() = checkGroundness()

    override val items: Iterable<Term>
        get() = toSequence().asIterable()

    override fun checkGroundness(): Boolean = unfoldedSequence.all { it.isGround }

    override fun argsHashCode(): Int = itemWiseHashCode(unfoldedSequence)

    override val variables: Sequence<Var>
        get() = unfoldedSequence.flatMap { it.variables }

    abstract override fun copyWithTags(tags: Map<String, Any>): Recursive

    override fun freshCopy(): Recursive = super.freshCopy().castToRecursive()

    override fun freshCopy(scope: Scope): Recursive = super.freshCopy(scope).castToRecursive()

    override fun itemsAreStructurallyEqual(other: Struct): Boolean =
        other.asRecursive()?.let {
            itemWiseEquals(unfoldedSequence, it.unfoldedSequence) { a, b ->
                a.structurallyEquals(b)
            }
        } ?: false

    override fun itemsAreEqual(
        other: Struct,
        useVarCompleteName: Boolean,
    ): Boolean =
        other.asRecursive()?.let {
            itemWiseEquals(unfoldedSequence, it.unfoldedSequence) { a, b ->
                a.equals(b, useVarCompleteName)
            }
        } ?: false

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitCollection(this)

    protected class LazyTwoItemsList<T>(
        firstGenerator: () -> T,
        secondGenerator: () -> T,
    ) : KtList<T> {
        private val fst: T by lazy(firstGenerator)

        private val snd: T by lazy(secondGenerator)

        override val size: Int
            get() = 2

        override fun contains(element: T): Boolean = fst == element || snd == element

        override fun containsAll(elements: KtCollection<T>): Boolean = elements.any { contains(it) }

        override fun get(index: Int): T =
            when (index) {
                0 -> fst
                1 -> snd
                else -> throw IndexOutOfBoundsException("Index out of range: $index")
            }

        override fun indexOf(element: T): Int =
            when (element) {
                fst -> 0
                snd -> 1
                else -> -1
            }

        override fun isEmpty(): Boolean = false

        override fun iterator(): Iterator<T> =
            iterator {
                yield(fst)
                yield(snd)
            }

        override fun lastIndexOf(element: T): Int =
            when (element) {
                snd -> 1
                fst -> 0
                else -> -1
            }

        override fun listIterator(): ListIterator<T> = listIterator(0)

        override fun listIterator(index: Int): ListIterator<T> =
            object : ListIterator<T> {
                private var currentIndex = index

                override fun hasNext(): Boolean = currentIndex < size

                override fun hasPrevious(): Boolean = currentIndex > 0

                override fun next(): T =
                    if (hasNext()) {
                        get(currentIndex++)
                    } else {
                        throw NoSuchElementException()
                    }

                override fun nextIndex(): Int = index + 1

                override fun previous(): T =
                    if (hasPrevious()) {
                        get(currentIndex--)
                    } else {
                        throw NoSuchElementException()
                    }

                override fun previousIndex(): Int = currentIndex - 1
            }

        override fun subList(
            fromIndex: Int,
            toIndex: Int,
        ): KtList<T> = (fromIndex until toIndex).map { get(it) }
    }
}
