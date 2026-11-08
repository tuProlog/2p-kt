package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.solve.ResolutionCounters
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.intercept.TransitionInterceptor
import it.unibo.tuprolog.solve.search.GoalSelectionPolicy
import it.unibo.tuprolog.solve.search.RuleSelectionPolicy
import it.unibo.tuprolog.utils.time.ChronometerFactory
import kotlin.time.Duration

internal data class SolveOptionsImpl(
    override val isLazy: Boolean,
    override val timeout: Duration = SolveOptions.MAX_TIMEOUT,
    override val limit: Int = SolveOptions.ALL_SOLUTIONS,
    override val chronometerFactory: ChronometerFactory? = null,
    override val customOptions: Map<String, Any> = emptyMap(),
    override val counters: ResolutionCounters? = null,
    override val interceptor: TransitionInterceptor? = null,
    override val ruleSelection: RuleSelectionPolicy? = null,
    override val goalSelection: GoalSelectionPolicy? = null,
) : SolveOptions {
    init {
        require(!timeout.isNegative()) { "Invalid timeout: $timeout" }
        require(limit != 0) { "Invalid limit: $limit" }
    }

    override fun setLazy(value: Boolean): SolveOptions = copy(isLazy = value)

    override fun setTimeout(value: Duration): SolveOptions = copy(timeout = value)

    override fun setChronometerFactory(value: ChronometerFactory?): SolveOptions = copy(chronometerFactory = value)

    override fun setCounters(value: ResolutionCounters?): SolveOptions = copy(counters = value)

    override fun setInterceptor(value: TransitionInterceptor?): SolveOptions = copy(interceptor = value)

    override fun setRuleSelection(value: RuleSelectionPolicy?): SolveOptions = copy(ruleSelection = value)

    override fun setGoalSelection(value: GoalSelectionPolicy?): SolveOptions = copy(goalSelection = value)

    override fun setLimit(value: Int): SolveOptions = copy(limit = value)

    override fun <X : Any> setOptions(options: Map<String, X>): SolveOptions = copy(customOptions = options)
}
