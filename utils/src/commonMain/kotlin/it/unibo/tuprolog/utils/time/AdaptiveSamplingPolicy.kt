package it.unibo.tuprolog.utils.time

import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configures how often an adaptive [Chronometer] (see [ChronometerFactory.adaptive]) reads the clock.
 *
 * At each reading, the chronometer updates its estimate of the average duration of a leap, as an exponentially
 * weighted moving average with weight [smoothing] for the latest observation, then plans the next reading after
 * as many leaps as fit in the sampling window, i.e. the least among [samplingPeriod] and a [budgetFraction] of the
 * remaining time. That leap count is clamped between [minLeaps] and [maxLeaps], and grows by at most
 * [maxGrowthFactor] between consecutive readings, so that a coarse clock (one observing no time elapsing across
 * leaps) cannot make it jump straight to [maxLeaps].
 */
data class AdaptiveSamplingPolicy(
    val samplingPeriod: Duration = 1.milliseconds,
    val budgetFraction: Double = 0.1,
    val minLeaps: Long = 1,
    val maxLeaps: Long = 4096,
    val maxGrowthFactor: Long = 2,
    val smoothing: Double = 0.25,
) {
    init {
        require(samplingPeriod.isPositive()) { "samplingPeriod must be positive: $samplingPeriod" }
        require(budgetFraction > 0.0 && budgetFraction <= 1.0) { "budgetFraction must be in (0, 1]: $budgetFraction" }
        require(minLeaps >= 1) { "minLeaps must be at least 1: $minLeaps" }
        require(maxLeaps >= minLeaps) { "maxLeaps must be at least minLeaps ($minLeaps): $maxLeaps" }
        require(maxGrowthFactor >= 1) { "maxGrowthFactor must be at least 1: $maxGrowthFactor" }
        require(smoothing > 0.0 && smoothing <= 1.0) { "smoothing must be in (0, 1]: $smoothing" }
    }

    companion object {
        /** The default policy. */
        @JvmStatic
        val DEFAULT: AdaptiveSamplingPolicy = AdaptiveSamplingPolicy()
    }
}
