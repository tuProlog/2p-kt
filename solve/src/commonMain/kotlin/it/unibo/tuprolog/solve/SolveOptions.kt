package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.impl.SolveOptionsImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SolveOptions {
    @JsName("isLazy")
    val isLazy: Boolean

    @JsName("isEager")
    val isEager: Boolean
        get() = !isLazy

    @JsName("timeout")
    val timeout: TimeDuration

    @JsName("limit")
    val limit: Int

    @JsName("isLimited")
    val isLimited: Boolean
        get() = limit >= 0

    @JsName("customOptions")
    val customOptions: Map<String, Any>

    @JsName("setLazy")
    fun setLazy(value: Boolean): SolveOptions

    @JsName("setTimeout")
    fun setTimeout(value: TimeDuration): SolveOptions

    @JsName("setLimit")
    fun setLimit(value: Int): SolveOptions

    @JsName("setOptions")
    fun <X : Any> setOptions(options: Map<String, X>): SolveOptions

    @JsName("addOptions")
    fun <X : Any> addOptions(options: Map<String, X>): SolveOptions =
        setOptions(customOptions + options)

    @JsName("setOption")
    fun <X : Any> setOption(key: String, value: X): SolveOptions =
        setOptions(customOptions.toMutableMap().also { it[key] = value })

    @JsName("addOption")
    fun <X : Any> addOption(key: String, value: X): SolveOptions =
        addOptions(mapOf(key to (value as Any)))

    companion object {
        const val MAX_TIMEOUT: Long = Long.MAX_VALUE
        const val ALL_SOLUTIONS: Int = -1

        @JsName("DEFAULT")
        @JvmStatic
        val DEFAULT: SolveOptions = allLazily()

        @JsName("ofWithOptions")
        @JvmStatic
        fun <X : Any> of(
            lazy: Boolean,
            timeout: TimeDuration = MAX_TIMEOUT,
            limit: Int = ALL_SOLUTIONS,
            customOption: Pair<String, X>,
            vararg customOptions: Pair<String, X>
        ): SolveOptions = SolveOptionsImpl(lazy, timeout, limit, arrayOf(customOption, *customOptions).toMap())

        @JsName("of")
        @JvmStatic
        fun of(lazy: Boolean, timeout: TimeDuration = MAX_TIMEOUT, limit: Int = ALL_SOLUTIONS): SolveOptions =
            SolveOptionsImpl(lazy, timeout, limit)

        @JsName("allLazily")
        @JvmStatic
        fun allLazily(): SolveOptions = of(true)

        @JsName("someLazily")
        @JvmStatic
        fun someLazily(limit: Int): SolveOptions = of(true, limit = limit)

        @JsName("allLazilyWithTimeout")
        @JvmStatic
        fun allLazilyWithTimeout(timeout: TimeDuration): SolveOptions = of(true, timeout = timeout)

        @JsName("someLazilyWithTimeout")
        @JvmStatic
        fun someLazilyWithTimeout(limit: Int, timeout: TimeDuration): SolveOptions =
            of(true, timeout = timeout, limit = limit)

        @JsName("allEagerly")
        @JvmStatic
        fun allEagerly(): SolveOptions = of(false)

        @JsName("someEagerly")
        @JvmStatic
        fun someEagerly(limit: Int): SolveOptions = of(false, limit = limit)

        @JsName("allEagerlyWithTimeout")
        @JvmStatic
        fun allEagerlyWithTimeout(timeout: TimeDuration): SolveOptions = of(false, timeout = timeout)

        @JsName("someEagerlyWithTimeout")
        @JvmStatic
        fun someEagerlyWithTimeout(limit: Int, timeout: TimeDuration): SolveOptions =
            of(false, timeout = timeout, limit = limit)
    }
}
