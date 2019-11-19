package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SetImpl
import kotlin.collections.List as KtList

interface Set : Struct {

    override val isSet: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    val unfoldedSequence: Sequence<Term>

    val unfoldedList: KtList<Term>

    val unfoldedArray: Array<Term>

    fun toArray(): Array<Term> = unfoldedArray

    fun toList(): KtList<Term> = unfoldedList

    fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Set = super.freshCopy() as Set

    override fun freshCopy(scope: Scope): Set =
        when {
            isGround -> this
            else -> scope.setOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
        }

    companion object {
        const val FUNCTOR = "{}"

        fun empty(): EmptySet = EmptySet()

        fun of(vararg terms: Term): Set = of(terms.toList())

        fun of(terms: KtList<Term>): Set =
            when {
                terms.isEmpty() -> empty()
                terms.size == 1 -> SetImpl(terms.single())
                else -> SetImpl(Tuple.of(terms))
            }

        fun of(terms: Iterable<Term>): Set = of(terms.toList())

        fun of(terms: Sequence<Term>): Set = of(terms.toList())
    }
}

