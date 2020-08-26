package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.flags.Unknown

/** A storage for flags and their values */
class PrologFlags private constructor(private val flags: Map<String, Term>) : Map<String, Term> by flags {

    private constructor(vararg flagValues: Pair<String, Term>) : this(mapOf(*flagValues))

    private constructor(vararg notableFlagValues: NotableFlag) : this(notableFlagValues.map { it.toPair() }.toMap())

    operator fun plus(flagValue: Pair<String, Term>): PrologFlags =
        PrologFlags(mapOf(flagValue) + this)

    operator fun plus(notableFlagValue: NotableFlag): PrologFlags =
        PrologFlags(mapOf(notableFlagValue.toPair()) + this)

    operator fun plus(flags: Map<String, Term>): PrologFlags =
        PrologFlags(this.flags + flags)

    operator fun minus(flagName: String): PrologFlags =
        PrologFlags(this.flags - flagName)

    operator fun minus(flagNames: Iterable<String>): PrologFlags =
        PrologFlags(this.flags - flagNames)

    companion object {
        fun empty() = PrologFlags(emptyMap())

        fun default() = PrologFlags(
            Unknown
        )

        fun of(flags: Map<String, Term>) = PrologFlags(flags)

        fun of(vararg flagValues: Pair<String, Term>) = PrologFlags(*flagValues)

        fun of(vararg notableFlagValues: NotableFlag) = PrologFlags(*notableFlagValues)
    }
}
