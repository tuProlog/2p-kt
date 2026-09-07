package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A typed, well-known Prolog flag, i.e. a named switch whose legal values and default are known ahead of time --
 * as opposed to the arbitrary `String → `[Term]` entries a plain [FlagStore] can also hold. Implemented as `object`s
 * for both ISO-standard flags ([Unknown], [DoubleQuotes]) and implementation-specific ones ([LastCallOptimization],
 * [TrackVariables], [MaxArity]).
 *
 * @see FlagStore
 */
interface NotableFlag {
    /** This flag's name, as it appears as the key in a [FlagStore] (e.g. `"unknown"`, `"double_quotes"`). */
    @JsName("name")
    val name: String

    /** The value this flag takes unless explicitly set otherwise. */
    @JsName("defaultValue")
    val defaultValue: Term

    /** Whether this flag's value can be changed at all (`false` for e.g. [MaxArity]). */
    @JsName("isEditable")
    val isEditable: Boolean
        get() = true

    /** Every legal value this flag may take. */
    @JsName("admissibleValues")
    val admissibleValues: Sequence<Term>

    /** Whether [value] is one of [admissibleValues]. */
    @JsName("isAdmissibleValue")
    fun isAdmissibleValue(value: Term): Boolean = value in admissibleValues

    /** This flag's [name] paired with its [defaultValue], ready to be inserted into a [FlagStore]. */
    @JsName("toPair")
    fun toPair(): Pair<String, Term> = this to defaultValue

    /**
     * This flag's [name] paired with [value], ready to be inserted into a [FlagStore].
     * @throws IllegalArgumentException if [value] is not among [admissibleValues].
     */
    @JsName("to")
    infix fun to(value: Term): Pair<String, Term> =
        name to
            value.also {
                require(isAdmissibleValue(it)) {
                    "$value is not an admissible value for flag $name"
                }
            }

    companion object {
        /** Looks up the built-in [NotableFlag] ([DoubleQuotes], [LastCallOptimization], [MaxArity], [Unknown]) named [name], or `null` if none matches. */
        @JsName("fromName")
        @JvmStatic
        fun fromName(name: String): NotableFlag? =
            sequenceOf(
                DoubleQuotes,
                LastCallOptimization,
                MaxArity,
                Unknown,
            ).firstOrNull { it.name == name }
    }
}
