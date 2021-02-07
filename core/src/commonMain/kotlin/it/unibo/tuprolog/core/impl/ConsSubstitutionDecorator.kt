package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.setTags

class ConsSubstitutionDecorator(
    private val cons: Cons,
    private val unifier: Substitution.Unifier
) : Cons {
    override val head: Term
        get() = cons.head

    override val tail: Term
        get() = cons.tail

    override fun freshCopy(): Cons = freshCopy(Scope.empty())

    override fun freshCopy(scope: Scope): Cons = when {
        isGround -> this
        isWellFormed -> scope.listOf(toSequence().map { it.freshCopy(scope) }).setTags(tags) as Cons
        else -> scope.listFrom(
            unfoldedList.subList(0, unfoldedList.lastIndex).map { it.freshCopy(scope) },
            last = unfoldedList.last().freshCopy(scope)
        ).setTags(tags) as Cons
    }

    override val isWellFormed: Boolean
        get() = TODO("Not yet implemented")

    override val last: Term
        get() = TODO("Not yet implemented")

    override val unfoldedSequence: Sequence<Term>
        get() = TODO("Not yet implemented")

    override val unfoldedList: List<Term>
        get() = TODO("Not yet implemented")

    override val unfoldedArray: Array<Term>
        get() = TODO("Not yet implemented")

    override fun toArray(): Array<Term> {
        TODO("Not yet implemented")
    }

    override fun toList(): List<Term> {
        TODO("Not yet implemented")
    }

    override fun toSequence(): Sequence<Term> {
        TODO("Not yet implemented")
    }

    override fun unfold(): Sequence<Term> {
        TODO("Not yet implemented")
    }

    override fun addLast(argument: Term): Struct {
        TODO("Not yet implemented")
    }

    override fun addFirst(argument: Term): Struct {
        TODO("Not yet implemented")
    }

    override fun insertAt(index: Int, argument: Term): Struct {
        TODO("Not yet implemented")
    }

    override fun setFunctor(functor: String): Struct {
        TODO("Not yet implemented")
    }

    override val isFunctorWellFormed: Boolean
        get() = TODO("Not yet implemented")

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun structurallyEquals(other: Term): Boolean {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override val tags: Map<String, Any>
        get() = TODO("Not yet implemented")

    override fun replaceTags(tags: Map<String, Any>): Term {
        TODO("Not yet implemented")
    }
}
