package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SetImpl

interface Set : Struct {

    override val isSet: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = arity == 0

    override val functor: String
        get() = FUNCTOR

    fun toArray(): Array<Term> = args

    fun toList(): kotlin.collections.List<Term> = args.toList()

    fun toSequence(): Sequence<Term> = args.asSequence()

    override fun freshCopy(): Set = super.freshCopy() as Set

    override fun freshCopy(scope: Scope): Set =
            if (isGround) {
                this
            } else {
                scope.setOf(argsSequence.map { it.freshCopy(scope) }.asIterable())
            }

    companion object {
        const val FUNCTOR = "{}"

        fun empty(): EmptySet = EmptySet()

        fun of(vararg terms: Term): Set =
                when {
                    terms.isEmpty() -> empty()
                    else -> SetImpl(arrayOf(*terms))
                }

        fun of(terms: Collection<Term>): Set =
                when {
                    terms.isEmpty() -> empty()
                    else -> SetImpl(terms.toTypedArray())
                }

        fun of(terms: Iterable<Term>): Set = of(terms.toList())
    }
}

