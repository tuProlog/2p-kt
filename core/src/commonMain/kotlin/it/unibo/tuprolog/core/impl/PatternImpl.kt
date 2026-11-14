package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.utils.itemWiseHashCode
import it.unibo.tuprolog.utils.setTags
import kotlin.concurrent.Volatile

@Suppress("EqualsOrHashCode")
internal class PatternImpl(
    override val functor: Var,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap(),
) : TermImpl(tags),
    Pattern {
    init {
        if (!anyCreated) anyCreated = true
    }

    override fun copyWithTags(tags: Map<String, Any>): Pattern = PatternImpl(functor, args, tags)

    override fun freshCopy(): Pattern = Scope.withQuickScope { freshCopy(this) }

    override fun freshCopy(scope: Scope): Pattern =
        scope.patternOf(functor.freshCopy(scope), args.map { it.freshCopy(scope) }).setTags(tags)

    override fun setArgs(vararg args: Term): Pattern = Pattern.of(functor, *args)

    override fun setArgs(args: Iterable<Term>): Pattern = Pattern.of(functor, args)

    override fun setArgs(args: Sequence<Term>): Pattern = Pattern.of(functor, args)

    override fun setFunctor(functor: Var): Pattern = Pattern.of(functor, args)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term {
        val newFunctor = functor.apply(unifier)
        var changed = newFunctor !== functor
        val newArgs = args.map { arg -> arg.apply(unifier).also { if (it !== arg) changed = true } }
        return when {
            !changed -> this
            newFunctor.isVar -> Pattern.of(newFunctor.castToVar(), newArgs).setTags(tags)
            newFunctor.isAtom -> Struct.of(newFunctor.castToAtom().value, newArgs).setTags(tags)
            else -> throw SubstitutionApplicationException(
                this,
                unifier,
                "Cannot apply $unifier to $this: functor $functor would be bound to $newFunctor, which is not an atom",
            )
        }
    }

    override fun structurallyEquals(other: Term): Boolean =
        other.asPattern()?.let { that ->
            arity == that.arity && (0 until arity).all { args[it].structurallyEquals(that[it]) }
        } ?: false

    override fun equals(other: Any?): Boolean = asTerm(other)?.let { equals(it, true) } ?: false

    override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean =
        other.asPattern()?.let { that ->
            this === that ||
                (
                    arity == that.arity &&
                        functor.equals(that.functor, useVarCompleteName) &&
                        (0 until arity).all { args[it].equals(that[it], useVarCompleteName) }
                )
        } ?: false

    override val hashCodeCache: Int by lazy {
        31 * (31 * functor.hashCode() + arity) + itemWiseHashCode(args)
    }

    override fun toString(): String = "$functor(${args.joinToString(", ")})"

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitPattern(this)

    companion object {
        // a process-wide hint, never affecting results: while false, no term contains a pattern
        @Volatile
        var anyCreated: Boolean = false
            private set
    }
}
