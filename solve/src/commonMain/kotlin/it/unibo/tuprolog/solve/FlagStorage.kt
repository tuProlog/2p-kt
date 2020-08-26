package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.flags.Unknown

/** A storage for flags and their values */
class FlagStorage private constructor(private val flags: Map<String, Term>) : Map<String, Term> by flags {

    private constructor(vararg flagValues: Pair<String, Term>) : this(mapOf(*flagValues))

    private constructor(vararg notableFlagValues: NotableFlag) : this(notableFlagValues.map { it.toPair() }.toMap())

    operator fun plus(flagValue: Pair<String, Term>): FlagStorage =
        FlagStorage(mapOf(flagValue) + this)

    operator fun plus(notableFlagValue: NotableFlag): FlagStorage =
        FlagStorage(mapOf(notableFlagValue.toPair()) + this)

    operator fun plus(flags: Map<String, Term>): FlagStorage =
        FlagStorage(this.flags + flags)

    operator fun minus(flagName: String): FlagStorage =
        FlagStorage(this.flags - flagName)

    operator fun minus(flagNames: Iterable<String>): FlagStorage =
        FlagStorage(this.flags - flagNames)

    companion object {
        fun empty() = FlagStorage(emptyMap())

        fun default() = FlagStorage(
            Unknown
        )

        fun of(flags: Map<String, Term>) = FlagStorage(flags)

        fun of(vararg flagValues: Pair<String, Term>) = FlagStorage(*flagValues)

        fun of(vararg notableFlagValues: NotableFlag) = FlagStorage(*notableFlagValues)
    }
}
