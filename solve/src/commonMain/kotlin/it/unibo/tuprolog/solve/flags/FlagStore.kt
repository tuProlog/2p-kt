package it.unibo.tuprolog.solve.flags

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * An immutable storage for Prolog flags and their values, i.e. the `String → `[Term]` map a
 * [it.unibo.tuprolog.solve.Solver] reads (via [it.unibo.tuprolog.solve.ExecutionContextAware.flags]) to answer
 * `current_prolog_flag/2` and similar. Keeping flags as data, rather than as scattered solver fields, makes the
 * whole configurable surface of a solver enumerable and snapshot-able as part of an
 * [it.unibo.tuprolog.solve.ExecutionContext].
 *
 * Every mutator ([set], [plus], [minus]) returns a new [FlagStore] rather than changing this one in place. Flags can
 * be addressed either by plain [String] name, or by a [NotableFlag] (a typed, ISO-standard or implementation-defined
 * flag such as [Unknown] or [DoubleQuotes]), which additionally validates the assigned value against
 * [NotableFlag.admissibleValues].
 *
 * @see NotableFlag
 * @see DEFAULT
 */
data class FlagStore(
    private val flags: Map<String, Term>,
) : Map<String, Term> by flags {
    /** Looks up the value of [notableFlag] by its [NotableFlag.name], or `null` if unset. */
    @JsName("get")
    operator fun get(notableFlag: NotableFlag): Term? = this[notableFlag.name]

    /** Returns a copy of this store with the flag named [name] set to [value] (added, or replacing any previous value). */
    @JsName("set")
    operator fun set(
        name: String,
        value: Term,
    ): FlagStore = plus(name, value)

    /** Returns a copy of this store with [notableFlag] set to its [NotableFlag.defaultValue]. */
    @JsName("setNotableToDefault")
    fun set(notableFlag: NotableFlag): FlagStore = set(notableFlag.name, notableFlag.defaultValue)

    /**
     * Returns a copy of this store with [notableFlag] set to [value].
     * @throws IllegalArgumentException if [value] is not among [notableFlag]'s [NotableFlag.admissibleValues].
     */
    @JsName("setNotable")
    operator fun set(
        notableFlag: NotableFlag,
        value: Term,
    ): FlagStore =
        if (value in notableFlag.admissibleValues) {
            set(notableFlag.name, value)
        } else {
            throw IllegalArgumentException("Value $value is not admissible for flag $notableFlag")
        }

    /** Same as [set], as a regularly-named method rather than an operator. */
    @JsName("plus")
    fun plus(
        name: String,
        value: Term,
    ): FlagStore = this + (name to value)

    /** Returns a copy of this store with the flag named [flagValue]'s first component set to its second component. */
    @JsName("plusPair")
    operator fun plus(flagValue: Pair<String, Term>): FlagStore = FlagStore(this.flags + mapOf(flagValue))

    /** Returns a copy of this store with [notableFlagValue] set to its [NotableFlag.defaultValue]. */
    @JsName("plusNotable")
    operator fun plus(notableFlagValue: NotableFlag): FlagStore =
        FlagStore(this.flags + mapOf(notableFlagValue.toPair()))

    /** Returns a copy of this store with every entry of [flags] set (added, or overriding existing ones). */
    @JsName("plusMap")
    operator fun plus(flags: Map<String, Term>): FlagStore = FlagStore(this.flags + flags)

    /** Returns a copy of this store without the flag named [flagName]. */
    @JsName("minus")
    operator fun minus(flagName: String): FlagStore = FlagStore(this.flags - flagName)

    /** Returns a copy of this store without any of the flags named in [flagNames]. */
    @JsName("minusMany")
    operator fun minus(flagNames: Iterable<String>): FlagStore = FlagStore(this.flags - flagNames)

    companion object {
        /** The empty [FlagStore], with no flag set. */
        @JvmField
        val EMPTY = FlagStore(emptyMap())

        /** Creates a [FlagStore] out of the given name-value pairs. */
        @JvmStatic
        @JsName("ofPair")
        fun of(vararg flagValues: Pair<String, Term>) = FlagStore(mapOf(*flagValues))

        /**
         * The default [FlagStore] every new [it.unibo.tuprolog.solve.Solver] starts with (see
         * [it.unibo.tuprolog.solve.SolverFactory.defaultFlags]), with every [NotableFlag] set to its
         * [NotableFlag.defaultValue]: [Unknown], [MaxArity], [DoubleQuotes], [LastCallOptimization],
         * [TrackVariables].
         */
        @JvmField
        val DEFAULT =
            FlagStore.of(
                Unknown,
                MaxArity,
                DoubleQuotes,
                LastCallOptimization,
                TrackVariables,
            )

        /** Same as [EMPTY], as a factory method. */
        @JsName("empty")
        @JvmStatic
        fun empty() = EMPTY

        /** Creates a [FlagStore] out of the given [flags] map. */
        @JsName("ofMap")
        @JvmStatic
        fun of(flags: Map<String, Term>) = FlagStore(flags)

        /**
         * Creates a [FlagStore] setting every one of [notableFlagValues] to its [NotableFlag.defaultValue].
         *
         * Uses `map { }.toMap()` rather than `associate { }`: the latter reproducibly lost every entry but the
         * last one on Kotlin/JS here, collapsing them into a single `{undefined: undefined}` entry - see
         * `it.unibo.tuprolog.ui.gui.prolog.SolverFactoryProfileTest`, which failed on the JS target only until
         * this was changed; reproduced down to `Array<NotableFlag>.associate { it.toPair() }` specifically.
         */
        @JsName("of")
        @JvmStatic
        fun of(vararg notableFlagValues: NotableFlag): FlagStore =
            FlagStore(notableFlagValues.map { it.toPair() }.toMap())
    }
}
