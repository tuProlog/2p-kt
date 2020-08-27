package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.DoubleQuotes
import it.unibo.tuprolog.solve.flags.MaxArity
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.flags.Unknown

/** A storage for flags and their values */
class FlagStore private constructor(private val flags: Map<String, Term>) : Map<String, Term> by flags {

    private constructor(vararg flagValues: Pair<String, Term>) : this(mapOf(*flagValues))

    private constructor(vararg notableFlagValues: NotableFlag) : this(notableFlagValues.map { it.toPair() }.toMap())

    operator fun get(notableFlag: NotableFlag): Term? =
        this[notableFlag.name]

    operator fun plus(flagValue: Pair<String, Term>): FlagStore =
        FlagStore(mapOf(flagValue) + this)

    operator fun plus(notableFlagValue: NotableFlag): FlagStore =
        FlagStore(mapOf(notableFlagValue.toPair()) + this)

    operator fun plus(flags: Map<String, Term>): FlagStore =
        FlagStore(this.flags + flags)

    operator fun minus(flagName: String): FlagStore =
        FlagStore(this.flags - flagName)

    operator fun minus(flagNames: Iterable<String>): FlagStore =
        FlagStore(this.flags - flagNames)

    companion object {
        val EMPTY = FlagStore(emptyMap())

        val DEFAULT = FlagStore(
            Unknown,
            MaxArity,
            DoubleQuotes
        )

        fun of(flags: Map<String, Term>) = FlagStore(flags)

        fun of(vararg flagValues: Pair<String, Term>) = FlagStore(*flagValues)

        fun of(vararg notableFlagValues: NotableFlag) = FlagStore(*notableFlagValues)
    }
}
