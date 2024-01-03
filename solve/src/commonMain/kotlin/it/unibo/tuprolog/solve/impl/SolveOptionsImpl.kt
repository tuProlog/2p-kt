package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.TimeDuration

internal data class SolveOptionsImpl(
    override val isLazy: Boolean,
    override val timeout: TimeDuration = SolveOptions.MAX_TIMEOUT,
    override val limit: Int = SolveOptions.ALL_SOLUTIONS,
    override val customOptions: Map<String, Any> = emptyMap(),
) : SolveOptions {
    init {
        require(timeout >= 0L) { "Invalid timeout: $timeout" }
        require(limit != 0) { "Invalid limit: $limit" }
    }

    override fun setLazy(value: Boolean): SolveOptions = copy(isLazy = value)

    override fun setTimeout(value: TimeDuration): SolveOptions = copy(timeout = value)

    override fun setLimit(value: Int): SolveOptions = copy(limit = value)

    override fun <X : Any> setOptions(options: Map<String, X>): SolveOptions = copy(customOptions = options)
}
