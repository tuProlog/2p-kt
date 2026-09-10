package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.impl.SolveOptionsImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Configuration for a single [Solver.solve] invocation: whether solutions are computed lazily or eagerly, how long
 * resolution may run for, how many solutions to cap at, and any implementation-specific extra knob.
 *
 * Instances are immutable; the `set*`/`add*` methods all return a new [SolveOptions] rather than mutating the
 * receiver. Build one via the companion's factories -- [allLazily], [someLazily], [allEagerly], [someEagerly], their
 * `...WithTimeout` variants, or the general [of] -- or start from [DEFAULT] and tweak it, e.g.
 * `SolveOptions.DEFAULT.setLimit(1)`.
 *
 * @see Solver.solve
 */
interface SolveOptions {
    /**
     * Whether [Solver.solve] should stream solutions on demand as the returned [Sequence] is consumed (`true`), or
     * compute the whole (bounded) solution set eagerly before returning (`false`, see [isEager]).
     */
    @JsName("isLazy")
    val isLazy: Boolean

    /** The negation of [isLazy]: `true` if solutions are computed eagerly, ahead of consuming the sequence. */
    @JsName("isEager")
    val isEager: Boolean
        get() = !isLazy

    /** The maximum duration resolution is allowed to run for, before being aborted; defaults to [MAX_TIMEOUT]. */
    @JsName("timeout")
    val timeout: TimeDuration

    /**
     * The maximum number of solutions to produce, or [ALL_SOLUTIONS] (i.e. `-1`) for no cap.
     * @see isLimited
     */
    @JsName("limit")
    val limit: Int

    /** Whether [limit] actually caps the number of produced solutions (i.e. [limit] is not [ALL_SOLUTIONS]). */
    @JsName("isLimited")
    val isLimited: Boolean
        get() = limit >= 0

    /** Implementation-specific extra options, keyed by name, that a particular [Solver] strategy may interpret. */
    @JsName("customOptions")
    val customOptions: Map<String, Any>

    /** Returns a copy of these options with [isLazy] set to [value]. */
    @JsName("setLazy")
    fun setLazy(value: Boolean): SolveOptions

    /** Returns a copy of these options with [timeout] set to [value]. */
    @JsName("setTimeout")
    fun setTimeout(value: TimeDuration): SolveOptions

    /** Returns a copy of these options with [limit] set to [value]. */
    @JsName("setLimit")
    fun setLimit(value: Int): SolveOptions

    /** Returns a copy of these options with [customOptions] replaced entirely by [options]. */
    @JsName("setOptions")
    fun <X : Any> setOptions(options: Map<String, X>): SolveOptions

    /** Returns a copy of these options with [customOptions] merged with (and overridden by) [options]. */
    @JsName("addOptions")
    fun <X : Any> addOptions(options: Map<String, X>): SolveOptions = setOptions(customOptions + options)

    /** Returns a copy of these options with a single custom option, [key], replaced by [value]. */
    @JsName("setOption")
    fun <X : Any> setOption(
        key: String,
        value: X,
    ): SolveOptions = setOptions(customOptions.toMutableMap().also { it[key] = value })

    /** Returns a copy of these options with a single custom option, [key], added (or overridden) with [value]. */
    @JsName("addOption")
    fun <X : Any> addOption(
        key: String,
        value: X,
    ): SolveOptions = addOptions(mapOf(key to (value as Any)))

    companion object {
        /** The default [timeout]: no practical limit. */
        const val MAX_TIMEOUT: Long = Long.MAX_VALUE

        /** The [limit] value denoting "no cap on the number of solutions". */
        const val ALL_SOLUTIONS: Int = -1

        /** The default [SolveOptions]: [allLazily], i.e. all solutions, lazily, without a timeout. */
        @JsName("DEFAULT")
        @JvmStatic
        val DEFAULT: SolveOptions = allLazily()

        /** Creates [SolveOptions] with the given [lazy]/[timeout]/[limit], and one or more [customOptions]. */
        @JsName("ofWithOptions")
        @JvmStatic
        fun <X : Any> of(
            lazy: Boolean,
            timeout: TimeDuration = MAX_TIMEOUT,
            limit: Int = ALL_SOLUTIONS,
            customOption: Pair<String, X>,
            vararg customOptions: Pair<String, X>,
        ): SolveOptions = SolveOptionsImpl(lazy, timeout, limit, arrayOf(customOption, *customOptions).toMap())

        /** Creates [SolveOptions] with the given [lazy]/[timeout]/[limit], and no custom options. */
        @JsName("of")
        @JvmStatic
        fun of(
            lazy: Boolean,
            timeout: TimeDuration = MAX_TIMEOUT,
            limit: Int = ALL_SOLUTIONS,
        ): SolveOptions = SolveOptionsImpl(lazy, timeout, limit)

        /** [SolveOptions] for lazily computing every solution ([ALL_SOLUTIONS]), without a timeout. */
        @JsName("allLazily")
        @JvmStatic
        fun allLazily(): SolveOptions = of(true)

        /** [SolveOptions] for lazily computing at most [limit] solutions, without a timeout. */
        @JsName("someLazily")
        @JvmStatic
        fun someLazily(limit: Int): SolveOptions = of(true, limit = limit)

        /** [SolveOptions] for lazily computing every solution, aborting after [timeout]. */
        @JsName("allLazilyWithTimeout")
        @JvmStatic
        fun allLazilyWithTimeout(timeout: TimeDuration): SolveOptions = of(true, timeout = timeout)

        /** [SolveOptions] for lazily computing at most [limit] solutions, aborting after [timeout]. */
        @JsName("someLazilyWithTimeout")
        @JvmStatic
        fun someLazilyWithTimeout(
            limit: Int,
            timeout: TimeDuration,
        ): SolveOptions = of(true, timeout = timeout, limit = limit)

        /** [SolveOptions] for eagerly computing every solution ([ALL_SOLUTIONS]), without a timeout. */
        @JsName("allEagerly")
        @JvmStatic
        fun allEagerly(): SolveOptions = of(false)

        /** [SolveOptions] for eagerly computing at most [limit] solutions, without a timeout. */
        @JsName("someEagerly")
        @JvmStatic
        fun someEagerly(limit: Int): SolveOptions = of(false, limit = limit)

        /** [SolveOptions] for eagerly computing every solution, aborting after [timeout]. */
        @JsName("allEagerlyWithTimeout")
        @JvmStatic
        fun allEagerlyWithTimeout(timeout: TimeDuration): SolveOptions = of(false, timeout = timeout)

        /** [SolveOptions] for eagerly computing at most [limit] solutions, aborting after [timeout]. */
        @JsName("someEagerlyWithTimeout")
        @JvmStatic
        fun someEagerlyWithTimeout(
            limit: Int,
            timeout: TimeDuration,
        ): SolveOptions = of(false, timeout = timeout, limit = limit)
    }
}
