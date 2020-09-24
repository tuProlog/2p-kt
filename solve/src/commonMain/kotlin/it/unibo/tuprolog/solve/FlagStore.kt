package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.DoubleQuotes
import it.unibo.tuprolog.solve.flags.MaxArity
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.flags.Unknown
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** A storage for flags and their values */
class FlagStore private constructor(private val flags: Map<String, Term>) : Map<String, Term> by flags {

    private constructor(vararg flagValues: Pair<String, Term>) : this(mapOf(*flagValues))

    private constructor(vararg notableFlagValues: NotableFlag) : this(notableFlagValues.map { it.toPair() }.toMap())

    @JsName("get")
    operator fun get(notableFlag: NotableFlag): Term? =
        this[notableFlag.name]

    @JsName("set")
    fun set(name: String, value: Term): FlagStore =
        plus(name, value)

    @JsName("setNotable")
    fun set(notableFlag: NotableFlag): FlagStore =
        set(notableFlag.name, notableFlag.defaultValue)

    @JsName("plus")
    fun plus(name: String, value: Term): FlagStore =
        this + (name to value)

    @JsName("plusPair")
    operator fun plus(flagValue: Pair<String, Term>): FlagStore =
        FlagStore(mapOf(flagValue) + this)

    @JsName("plusNotable")
    operator fun plus(notableFlagValue: NotableFlag): FlagStore =
        FlagStore(mapOf(notableFlagValue.toPair()) + this)

    @JsName("plusMap")
    operator fun plus(flags: Map<String, Term>): FlagStore =
        FlagStore(this.flags + flags)

    @JsName("minus")
    operator fun minus(flagName: String): FlagStore =
        FlagStore(this.flags - flagName)

    @JsName("minusMany")
    operator fun minus(flagNames: Iterable<String>): FlagStore =
        FlagStore(this.flags - flagNames)

    companion object {
        @JvmField
        val EMPTY = FlagStore(emptyMap())

        @JvmField
        val DEFAULT = FlagStore(
            Unknown,
            MaxArity,
            DoubleQuotes
        )

        @JsName("empty")
        @JvmStatic
        fun empty() = EMPTY

        @JsName("ofMap")
        @JvmStatic
        fun of(flags: Map<String, Term>) = FlagStore(flags)

        @JsName("ofPair")
        @JvmStatic
        fun of(vararg flagValues: Pair<String, Term>) = FlagStore(*flagValues)

        @JsName("of")
        @JvmStatic
        fun of(vararg notableFlagValues: NotableFlag) = FlagStore(*notableFlagValues)
    }
}
