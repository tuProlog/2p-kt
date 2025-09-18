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

    protected open fun checkGroundness(): Boolean = variables.none()

    override fun freshCopy(): Struct = freshCopy(Scope.empty())

    override fun freshCopy(scope: Scope): Struct =
        when {
            isGround -> this
            else -> scope.structOf(functor, argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    final override fun structurallyEquals(other: Term): Boolean =
        other.isStruct &&
            other.castToStruct().let {
                functor == it.functor && arity == it.arity && itemsAreStructurallyEqual(it)
            }

    @Suppress("RedundantAsSequence")
    protected open fun itemsAreStructurallyEqual(other: Struct): Boolean {
        for (i in 0 until arity) {
            if (!getArgAt(i).structurallyEquals(other[i])) {
                return false
            }
        }
        return true
    }

    override val isFunctorWellFormed: Boolean
        get() = Struct.isWellFormedFunctor(functor)

    final override fun equals(other: Any?): Boolean = asTerm(other)?.asStruct()?.let { equalsImpl(it, true) } ?: false

    @Suppress("RedundantAsSequence")
    protected open fun itemsAreEqual(
        other: Struct,
        useVarCompleteName: Boolean,
    ): Boolean {
        for (i in 0 until arity) {
            if (!getArgAt(i).equals(other[i], useVarCompleteName)) {
                return false
            }
        }
        return true
    }

    final override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = other.asStruct()?.let { equalsImpl(it, useVarCompleteName) } ?: false

    private fun equalsImpl(
        other: Struct,
        useVarCompleteName: Boolean,
    ): Boolean {
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

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        Struct.of(this.functor, this.args.map { it.apply(unifier) }).setTags(tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitStruct(this)
}
