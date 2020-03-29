package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SetImpl
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Set : Collection {

    override val isSet: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toList(): KtList<Term> = unfoldedList

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun freshCopy(): Set = super.freshCopy() as Set

    override fun freshCopy(scope: Scope): Set =
        when {
            isGround -> this
            else -> scope.setOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
        }

    companion object {

        const val FUNCTOR = "{}"

        @JvmStatic
        fun empty(): EmptySet = EmptySet()

        @JvmStatic
        fun of(vararg terms: Term): Set = of(terms.toList())

        @JvmStatic
        fun of(terms: KtList<Term>): Set =
            when {
                terms.isEmpty() -> empty()
                terms.size == 1 -> SetImpl(terms.single())
                else -> SetImpl(Tuple.of(terms))
            }

        @JvmStatic
        fun of(terms: Iterable<Term>): Set = of(terms.toList())

        @JvmStatic
        fun of(terms: Sequence<Term>): Set = of(terms.toList())
    }
}

