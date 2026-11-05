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
    override val unfoldedList: KtList<Term> by lazy { unfoldedSequence.toList() }

    override val unfoldedArray: Array<Term>
        get() = unfoldedList.toTypedArray()

    private val groundness: Lazy<Boolean> = lazy { checkGroundness() }

    private val sizeCache: Lazy<Int> = lazy { computeSize() }

    /**
     * Whether this term is ground, computed at most once, by an iterative traversal of [unfoldedSequence].
     * Subclasses knowing it in advance (i.e. [knownGround]) should override this property to return it.
     */
    override val isGround: Boolean
        get() = groundness.value

    override val size: Int
        get() = sizeCache.value

    /**
     * This term's groundness, if it is already known (i.e. it can be read without traversing any lazy
     * descendant), or `null` otherwise.
     */
    internal open val knownGround: Boolean?
        get() = groundness.valueIfInitialized()

    /** This term's [size], if it is already known, or `null` otherwise. See [knownGround]. */
    internal open val knownSize: Int?
        get() = sizeCache.valueIfInitialized()

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

    /** A list of two items, each computed lazily (and at most once) on first access. */
    protected class LazyTwoItemsList<T>(
        firstGenerator: () -> T,
        secondGenerator: () -> T,
    ) : AbstractList<T>() {
        private val fst: T by lazy(firstGenerator)
        private val snd: T by lazy(secondGenerator)

        override val size: Int
            get() = 2

        override fun get(index: Int): T =
            when (index) {
                0 -> fst
                1 -> snd
                else -> throw IndexOutOfBoundsException("Index out of range: $index")
            }
    }
}

internal fun <T> Lazy<T>.valueIfInitialized(): T? = if (isInitialized()) value else null

/** The groundness of [this] term, if known without traversing any lazy descendant, or `null` otherwise. */
internal val Term.knownGround: Boolean?
    get() = if (this is RecursiveImpl) knownGround else isGround

/** The size of [this] recursive term, if known without traversing any lazy descendant, or `null` otherwise. */
internal val Term.knownSize: Int?
    get() =
        when (this) {
            is RecursiveImpl -> knownSize
            is Recursive -> size
            else -> null
        }

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
