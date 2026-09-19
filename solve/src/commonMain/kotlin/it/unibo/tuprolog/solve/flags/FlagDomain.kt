package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.toTerm
import org.gciatto.kt.math.BigInteger

sealed interface FlagDomain : Iterable<Term> {
    operator fun contains(value: Term): Boolean

    data class SetOfTerms(val values: Set<Term>) : FlagDomain {
        constructor(vararg values: Term) : this(values.toSet())

        override fun contains(value: Term): Boolean = value in values

        override fun iterator(): Iterator<Term> = values.iterator()
    }

    data class Singleton(val value: Term) : FlagDomain {
        override fun contains(value: Term): Boolean = value == this.value

        override fun iterator(): Iterator<Term> = iterator { yield(value) }
    }

    data class IntRange(val minInclusive: BigInteger, val maxInclusive: BigInteger) : FlagDomain {

        constructor(min: Int, max: Int) : this(BigInteger.of(min), BigInteger.of(max))

        constructor(min: Term, max: Term) : this(min.castToNumeric().intValue, max.castToNumeric().intValue)

        override fun contains(value: Term): Boolean =
            value.asInteger()?.intValue?.let { it in minInclusive..maxInclusive } ?: false

        override fun iterator(): Iterator<Term> = iterator { yield(minInclusive.toTerm()) }
    }

    data class Enumerated(val values: List<Atom>) : FlagDomain {
        constructor(vararg values: Atom) : this(values.toList())

        override fun contains(value: Term): Boolean = value in values

        override fun iterator(): Iterator<Term> = values.iterator()
    }
}
