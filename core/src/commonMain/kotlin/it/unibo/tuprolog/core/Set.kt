package it.unibo.tuprolog.core

import kotlin.collections.List

interface Set : Struct {

    override val isSet: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    fun toArray(): Array<Term> {
        return args
    }

    fun toList(): List<Term> {
        return args.toList()
    }

    fun toSequence(): Sequence<Term> {
        return args.asSequence()
    }

    companion object {
        const val FUNCTOR = "{}"

        fun empty(): EmptySet = EmptySet()

        fun of(vararg terms: Term): Set {
            return when {
                terms.isEmpty() -> empty()
                else -> SetImpl(arrayOf(*terms))
            }
        }

        fun of(terms: List<Term>): Set {
            return when {
                terms.isEmpty() -> empty()
                else -> SetImpl(terms.toTypedArray())
            }
        }

        fun of(terms: Iterable<Term>): Set {
            return of(terms.toList())
        }

        fun of(terms: Sequence<Term>): Set {
            return of(terms.toList())
        }
    }
}

