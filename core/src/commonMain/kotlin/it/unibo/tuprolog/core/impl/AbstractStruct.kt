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
    override val isGround: Boolean
        get() = checkGroundness()

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

    override val hashCodeCache: Int by lazy {
        var result = functor.hashCode()
        result = 31 * result + arity
        result = 31 * result + argsHashCode()
        result
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
