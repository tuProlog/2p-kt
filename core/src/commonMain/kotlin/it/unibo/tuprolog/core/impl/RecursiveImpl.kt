package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Recursive
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.itemWiseHashCode
import kotlin.collections.List as KtList

internal abstract class RecursiveImpl(
    functor: String,
    args: KtList<Term>,
    tags: Map<String, Any>,
) : AbstractStruct(functor, args, tags),
    Recursive {
    private var cachedUnfoldedList: KtList<Term>? = null

    override val unfoldedList: KtList<Term>
        get() = cachedUnfoldedList ?: unfoldedSequence.toList().also { cachedUnfoldedList = it }

    override val unfoldedArray: Array<Term>
        get() = unfoldedList.toTypedArray()

    /**
     * This term's size, if already computed or known at construction, or -1 otherwise. Eager nodes set it at
     * construction when their children already know theirs, so that it propagates in O(1); otherwise, it is
     * computed on demand, iteratively, by [computeSize].
     */
    protected var cachedSize: Int = -1

    override val size: Int
        get() = if (cachedSize >= 0) cachedSize else computeSize().also { cachedSize = it }

    /**
     * This term's groundness, if it is already known (i.e. it can be read without traversing any lazy
     * descendant), or `null` otherwise.
     */
    internal val knownGround: Boolean?
        get() = cachedGround

    /** This term's [size], if it is already known, or -1 otherwise. See [knownGround]. */
    internal val knownSize: Int
        get() = cachedSize

    /** Computes [size] by traversing this term, iteratively. */
    protected open fun computeSize(): Int = unfoldedSequence.count()

    override fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = unifier.isEmpty() || knownGround == true

    override val items: Iterable<Term>
        get() = toSequence().asIterable()

    override fun checkGroundness(): Boolean = unfoldedSequence.all { it.isGround }

    override fun argsHashCode(): Int = itemWiseHashCode(unfoldedSequence)

    override val variables: Sequence<Var>
        get() = unfoldedSequence.flatMap { it.variables }

    abstract override fun copyWithTags(tags: Map<String, Any>): Recursive

    override fun freshCopy(): Recursive = super.freshCopy().castToRecursive()

    override fun freshCopy(scope: Scope): Recursive = super.freshCopy(scope).castToRecursive()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitCollection(this)

    /** The two children of a lazy node, which computes and memoizes them by itself. See [LazyArgs]. */
    protected open val firstChild: Term
        get() = args[0]

    protected open val secondChild: Term
        get() = args[1]

    /** The args of a lazy node: a view over its [firstChild] and [secondChild], computed on access. */
    protected inner class LazyArgs : AbstractList<Term>() {
        override val size: Int
            get() = 2

        override fun get(index: Int): Term =
            when (index) {
                0 -> firstChild
                1 -> secondChild
                else -> throw IndexOutOfBoundsException("Index out of range: $index")
            }
    }
}

/** The groundness of [this] term, if known without traversing any lazy descendant, or `null` otherwise. */
internal val Term.knownGround: Boolean?
    get() = if (this is RecursiveImpl) knownGround else isGround

/** The size of [this] recursive term, if known without traversing any lazy descendant, or -1 otherwise. */
internal val Term.knownSize: Int
    get() =
        when (this) {
            is RecursiveImpl -> knownSize
            is Recursive -> size
            else -> -1
        }

/** [size] + 1 if [size] is known (i.e. non-negative), or -1 otherwise. */
internal fun successorOfKnownSize(size: Int): Int = if (size >= 0) size + 1 else -1

/** The groundness of a term whose only children are [first] and [second], if known, or `null` otherwise. */
internal fun knownGroundOf(
    first: Term,
    second: Term,
): Boolean? {
    val a = first.knownGround
    if (a == false) return false
    val b = second.knownGround
    return when {
        b == false -> false
        a == true && b == true -> true
        else -> null
    }
}
