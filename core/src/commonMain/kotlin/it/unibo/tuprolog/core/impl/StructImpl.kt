package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

@Suppress("EqualsOrHashCode")
internal open class StructImpl(override val functor: String, override val args: Array<Term>) : TermImpl(), Struct {

    override val isGround: Boolean by lazy { super<Struct>.isGround }

    override val variables: Sequence<Var> by lazy { super.variables }

    override val indicator: Indicator by lazy { super.indicator }

    override val argsList: List<Term> by lazy { super.argsList }

    override val argsSequence: Sequence<Term> by lazy { super.argsSequence }

    override fun structurallyEquals(other: Term): Boolean =
        other is StructImpl &&
            functor == other.functor &&
            arity == other.arity &&
            (0 until arity).all { args[it] structurallyEquals other[it] }

    override val isFunctorWellFormed: Boolean by lazy {
        functor matches Struct.STRUCT_FUNCTOR_REGEX_PATTERN
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Struct) return false

        if (functor != other.functor) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    final override fun equals(other: Term, useVarCompleteName: Boolean): Boolean {
        if (other !is Struct) return false
        if (functor != other.functor) return false
        if (arity != other.arity) return false
        for (i in args.indices) {
            if (!args[i].equals(other.args[i], useVarCompleteName)) {
                return false
            }
        }
        return true
    }

    override val hashCodeCache: Int by lazy {
        var result = functor.hashCode()
        result = 31 * result + arity
        result = 31 * result + argsHashCode()
        result
    }

    protected open fun argsHashCode(): Int {
        return args.contentHashCode()
    }

    override fun toString(): String {
        return (
            if (isFunctorWellFormed) functor else Struct.escapeFunctor(functor)
            ) + (
            if (arity > 0) "(${args.joinToString(", ")})" else ""
            )
    }
}
