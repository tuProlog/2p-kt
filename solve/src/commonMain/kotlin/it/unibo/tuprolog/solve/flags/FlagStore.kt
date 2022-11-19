package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** A storage for flags and their values */
data class FlagStore(private val flags: Map<String, Term>) : Map<String, Term> by flags {

    @JsName("get")
    operator fun get(notableFlag: NotableFlag): Term? =
        this[notableFlag.name]

    @JsName("set")
    operator fun set(name: String, value: Term): FlagStore =
        plus(name, value)

    @JsName("setNotableToDefault")
    fun set(notableFlag: NotableFlag): FlagStore =
        set(notableFlag.name, notableFlag.defaultValue)

    @JsName("setNotable")
    operator fun set(notableFlag: NotableFlag, value: Term): FlagStore =
        if (value in notableFlag.admissibleValues) {
            set(notableFlag.name, value)
        } else {
            throw IllegalArgumentException("Value $value is not admissible for flag $notableFlag")
        }

    @JsName("plus")
    fun plus(name: String, value: Term): FlagStore =
        this + (name to value)

    @JsName("plusPair")
    operator fun plus(flagValue: Pair<String, Term>): FlagStore =
        FlagStore(this.flags + mapOf(flagValue))

    @JsName("plusNotable")
    operator fun plus(notableFlagValue: NotableFlag): FlagStore =
        FlagStore(this.flags + mapOf(notableFlagValue.toPair()))

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

        @JvmStatic
        @JsName("ofPair")
        fun of(vararg flagValues: Pair<String, Term>) = FlagStore(mapOf(*flagValues))

        @JvmField
        val DEFAULT = FlagStore.of(
            Unknown,
            MaxArity,
            DoubleQuotes,
            LastCallOptimization,
            TrackVariables
        )

        @JsName("empty")
        @JvmStatic
        fun empty() = EMPTY

        @JsName("ofMap")
        @JvmStatic
        fun of(flags: Map<String, Term>) = FlagStore(flags)

        @JsName("of")
        @JvmStatic
        fun of(vararg notableFlagValues: NotableFlag) = FlagStore(notableFlagValues.associate { it.toPair() })
    }
}
