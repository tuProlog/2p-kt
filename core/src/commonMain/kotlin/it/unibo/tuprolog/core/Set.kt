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
    }
}

