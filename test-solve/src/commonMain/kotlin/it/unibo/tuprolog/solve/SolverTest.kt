package it.unibo.tuprolog.solve

import it.unibo.tuprolog.utils.time.ChronometerFactory
import kotlin.time.Duration.Companion.microseconds

/**
 * The safety budgets ([shortBudget], [mediumBudget], [longBudget]) that conformance cases solve their queries with,
 * so that a query that should terminate but doesn't fails its test instead of hanging it (and the few cases expecting
 * a non-terminating query to time out get that timeout). Budgets count resolution steps, not real time (see
 * [stepBudget]), so they hold the same on any machine, however slow or busy. Shared solver contracts get them through
 * [ContractScope]; the per-module suites that still implement this interface (e.g. `:solve-concurrent`'s) get them
 * directly.
 *
 * Each budget is about 10 times what the queries using it took at most, as measured across every solver's suite when
 * these budgets were introduced: < 128 steps for [shortBudget], < 1024 for [mediumBudget], < 2048 for [longBudget].
 */
interface SolverTest {
    /** A budget for queries taking a few dozen resolution steps. */
    val shortBudget: SolveOptions
        get() = stepBudget(SHORT_BUDGET_STEPS)

    /** A budget for queries taking up to about a thousand resolution steps: the default. */
    val mediumBudget: SolveOptions
        get() = stepBudget(MEDIUM_BUDGET_STEPS)

    /** A budget for queries involving deeper search or recursion. */
    val longBudget: SolveOptions
        get() = stepBudget(LONG_BUDGET_STEPS)

    companion object {
        private const val SHORT_BUDGET_STEPS = 1_000L
        private const val MEDIUM_BUDGET_STEPS = 10_000L
        private const val LONG_BUDGET_STEPS = 100_000L

        /**
         * [SolveOptions] on simulated time where each resolution step takes 1 µs (and `sleep(N)` takes `N` ms),
         * timing out after [steps] such steps: a deterministic budget, independent of the host machine.
         */
        fun stepBudget(steps: Long): SolveOptions =
            SolveOptions
                .allLazilyWithTimeout(steps.microseconds)
                .setChronometerFactory(ChronometerFactory.simulated(durationPerLeap = 1.microseconds))
    }
}
