package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.utils.itemWiseHashCode
import it.unibo.tuprolog.utils.setTags

@Suppress("EqualsOrHashCode")
internal abstract class AbstractStruct(
    override val functor: String,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap(),
) : TermImpl(tags),
    Struct {
    // Caches below are plain fields filled on first use (racy single-check idiom, as in String.hashCode): their
    // values are immutable, so a race just computes them twice. Unlike `by lazy`, they cost no extra object.

    /** This term's groundness, if already computed (or known at construction), or `null` otherwise. */
    protected var cachedGround: Boolean? = null

    override val isGround: Boolean
        get() = cachedGround ?: checkGroundness().also { cachedGround = it }

    protected open fun checkGroundness(): Boolean = args.all { it.isGround }

    override fun freshCopy(): Struct = freshCopy(Scope.empty())

    override fun freshCopy(scope: Scope): Struct =
        when {
            isGround -> this
            else -> scope.structOf(functor, argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    final override fun structurallyEquals(other: Term): Boolean =
        other.isStruct && compareIteratively(this, other.castToStruct()) { a, b -> a.structurallyEquals(b) }

    override val isFunctorWellFormed: Boolean
        get() = Struct.isWellFormedFunctor(functor)

    final override fun equals(other: Any?): Boolean = asTerm(other)?.asStruct()?.let { equalsImpl(it, true) } ?: false

    final override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = other.asStruct()?.let { equalsImpl(it, useVarCompleteName) } ?: false

    private fun equalsImpl(
        other: Struct,
        useVarCompleteName: Boolean,
    ): Boolean = compareIteratively(this, other) { a, b -> a.equals(b, useVarCompleteName) }

    // 0 means "not computed yet": a term whose hash is actually 0 just recomputes it at every call
    private var cachedHashCode: Int = 0

    override val hashCodeCache: Int
        get() {
            if (cachedHashCode == 0) {
                var result = functor.hashCode()
                result = 31 * result + arity
                result = 31 * result + argsHashCode()
                cachedHashCode = result
            }
            return cachedHashCode
        }

    protected open fun argsHashCode(): Int = itemWiseHashCode(args)

    override fun toString(): String {
        val escaped = Struct.escapeFunctorIfNecessary(functor)
        val quoted = Struct.enquoteFunctorIfNecessary(escaped)
        return "$quoted${if (arity > 0) "(${args.joinToString(", ")})" else ""}"
    }

    override fun addLast(argument: Term): Struct = Struct.of(functor, args + argument)

    override fun addFirst(argument: Term): Struct = Struct.of(functor, listOf(argument) + args)

    override fun insertAt(
        index: Int,
        argument: Term,
    ): Struct =
        if (index in 0 until arity) {
            val argsArray = args.toTypedArray()
            Struct.of(
                functor,
                *argsArray.sliceArray(0 until index),
                argument,
                *argsArray.sliceArray(index until arity),
            )
        } else {
            throw IndexOutOfBoundsException("Index $index is out of bounds ${args.indices}")
        }

    override fun setFunctor(functor: String): Struct = Struct.of(functor, args)

    override fun setArgs(vararg args: Term): Struct = Struct.of(functor, *args)

    override fun setArgs(args: Iterable<Term>): Struct = Struct.of(functor, args)

    override fun setArgs(args: Sequence<Term>): Struct = Struct.of(functor, args)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term {
        var changed = false
        val newArgs = args.map { arg -> arg.apply(unifier).also { if (it !== arg) changed = true } }
        return if (changed) Struct.of(functor, newArgs).setTags(tags) else this
    }

    companion object {
        /**
         * Compares [first] and [second] argument-wise via [argsEqual], except for the last argument which, if a
         * [Struct] on both sides, is compared by looping rather than by recursion. Hence right-nested terms (lists,
         * tuples, blocks, `f(a, f(b, ...))`) take O(1) native stack. Identical subterms are equal without being
         * traversed, which also avoids forcing shared lazy suffixes.
         */
        internal inline fun compareIteratively(
            first: Struct,
            second: Struct,
            argsEqual: (Term, Term) -> Boolean,
        ): Boolean {
            var a = first
            var b = second
            while (true) {
                if (a === b) return true
                if (a.arity != b.arity || a.functor != b.functor) return false
                val last = a.arity - 1
                if (last < 0) return true
                for (i in 0 until last) {
                    if (!argsEqual(a[i], b[i])) return false
                }
                val x = a[last]
                val y = b[last]
                if (x !is Struct || y !is Struct) return argsEqual(x, y)
                a = x
                b = y
            }
        }
    }

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitStruct(this)
}
