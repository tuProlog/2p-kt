package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.visitors.whenStruct
import it.unibo.tuprolog.utils.setTags

@Suppress("EqualsOrHashCode")
internal open class StructImpl(
    override val functor: String,
    override val args: Array<Term>,
    tags: Map<String, Any> = emptyMap()
) : TermImpl(tags), Struct {

    override val isGround: Boolean by lazy { super<Struct>.isGround }

    override fun freshCopy(): Struct = freshCopy(Scope.empty())

    override fun freshCopy(scope: Scope): Struct = when {
        isGround -> this
        else -> scope.structOf(functor, argsSequence.map { it.freshCopy(scope) }).setTags(tags)
    }

    override fun copyWithTags(tags: Map<String, Any>): Struct =
        StructImpl(functor, args, tags)

    final override fun structurallyEquals(other: Term): Boolean =
        whenStruct(
            term = other,
            ifStruct = { functor == it.functor && arity == it.arity && itemsAreStructurallyEqual(it) },
            otherwise = { false }
        )

    protected open fun itemsAreStructurallyEqual(other: Struct): Boolean =
        (0 until arity).all { this[it] structurallyEquals other[it] }

    override val isFunctorWellFormed: Boolean
        get() = Struct.isWellFormedFunctor(functor)

    final override fun equals(other: Any?): Boolean =
        (other as? Struct)?.let { equalsImpl(it, true) } ?: false

    protected open fun itemsAreEqual(other: Struct, useVarCompleteName: Boolean): Boolean =
        (0 until arity).all { args[it].equals(other[it], useVarCompleteName) }

    final override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        (other as? Struct)?.let { equalsImpl(it, useVarCompleteName) } ?: false

    private fun equalsImpl(other: Struct, useVarCompleteName: Boolean): Boolean {
        if (this === other) return true
        if (functor != other.functor) return false
        if (arity != other.arity) return false
        if (!itemsAreEqual(other, useVarCompleteName)) return false
        return true
    }

    override val hashCodeCache: Int by lazy {
        var result = functor.hashCode()
        result = 31 * result + arity
        result = 31 * result + argsHashCode()
        result
    }

    protected open fun argsHashCode(): Int = args.contentHashCode()

    override fun toString(): String {
        val escaped = Struct.escapeFunctorIfNecessary(functor)
        val quoted = Struct.enquoteFunctorIfNecessary(escaped)
        return "$quoted${if (arity > 0) "(${args.joinToString(", ")})" else ""}"
    }

    override fun addLast(argument: Term): Struct = Struct.of(functor, *args, argument)

    override fun addFirst(argument: Term): Struct = Struct.of(functor, argument, *args)

    override fun insertAt(index: Int, argument: Term): Struct =
        if (index in args.indices) {
            Struct.of(
                functor,
                *args.sliceArray(0 until index),
                argument,
                *args.sliceArray(index..args.lastIndex)
            )
        } else {
            throw IndexOutOfBoundsException("Index $index is out of bounds ${args.indices}")
        }

    override fun setFunctor(functor: String): Struct = Struct.of(functor, *args)

    override fun setArgs(vararg args: Term): Struct = Struct.of(functor, *args)

    override fun setArgs(args: Iterable<Term>): Struct = Struct.of(functor, args)

    override fun setArgs(args: Sequence<Term>): Struct = Struct.of(functor, args)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        Struct.of(this.functor, this.argsList.map { it.apply(unifier) }).setTags(tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitStruct(this)
}
