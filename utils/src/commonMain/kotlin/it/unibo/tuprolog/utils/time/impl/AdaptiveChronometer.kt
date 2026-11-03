package it.unibo.tuprolog.utils.time.impl

import it.unibo.tuprolog.utils.time.AdaptiveSamplingPolicy
import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration

/**
 * A [Chronometer] reading the clock only every so many [leap]s, as planned by [policy] (see
 * [AdaptiveSamplingPolicy]), and never if the timeout is infinite. Only [leap]-driven readings feed the estimate
 * of the average leap duration: forced ones ([checkNow]) may follow arbitrarily long operations (e.g. a blocking
 * primitive), so they just re-plan the next reading from a fresh window.
 *
 * Under concurrent use, the sampling counters may race: that only shifts when the next reading happens. The
 * expiration flag and the deadline, which decide the outcome, are safely shared.
 */
internal class AdaptiveChronometer(
    timeline: Timeline,
    timeout: Duration,
    private val policy: AdaptiveSamplingPolicy,
    start: ComparableTimeMark? = null,
    expired: Boolean = false,
    scope: ChildScope? = null,
) : AbstractChronometer(timeline, timeout, start, expired, scope) {
    private var lastSample: ComparableTimeMark = startMark
    private var leapsSinceSample: Long = 0
    private var leapsBetweenSamples: Long = policy.minLeaps
    private var leapsUntilSample: Long = policy.minLeaps
    private var averageLeapNanos: Double = NO_ESTIMATE

    override fun leap(): Boolean {
        if (isExpired) return true
        if (isUnlimited) return false
        leapsSinceSample++
        if (--leapsUntilSample > 0) return false
        return sample(estimating = true)
    }

    override fun checkNow(): Boolean {
        if (isExpired) return true
        if (isUnlimited) return false
        return sample(estimating = false)
    }

    private fun sample(estimating: Boolean): Boolean {
        val now = read()
        if (expiredAt(now)) return true
        if (estimating && leapsSinceSample > 0) {
            val observed = (now - lastSample).inWholeNanoseconds.toDouble() / leapsSinceSample
            averageLeapNanos =
                if (averageLeapNanos < 0) {
                    observed
                } else {
                    policy.smoothing * observed + (1 - policy.smoothing) * averageLeapNanos
                }
        }
        plan(now)
        return false
    }

    private fun plan(now: ComparableTimeMark) {
        lastSample = now
        leapsSinceSample = 0
        val window = minOf(policy.samplingPeriod, (timeout - elapsedAt(now)) * policy.budgetFraction)
        val fitting =
            if (averageLeapNanos <= 0) Long.MAX_VALUE else (window.inWholeNanoseconds / averageLeapNanos).toLong()
        val growthCap =
            if (leapsBetweenSamples > Long.MAX_VALUE / policy.maxGrowthFactor) {
                Long.MAX_VALUE
            } else {
                leapsBetweenSamples * policy.maxGrowthFactor
            }
        leapsBetweenSamples = minOf(fitting, growthCap).coerceIn(policy.minLeaps, policy.maxLeaps)
        leapsUntilSample = leapsBetweenSamples
    }

    override fun onRestart(now: ComparableTimeMark) {
        lastSample = now
        leapsSinceSample = 0
        leapsBetweenSamples = policy.minLeaps
        leapsUntilSample = policy.minLeaps
        averageLeapNanos = NO_ESTIMATE
    }

    override fun newScope(
        timeout: Duration,
        start: ComparableTimeMark,
        expired: Boolean,
        scope: ChildScope,
    ): Chronometer = AdaptiveChronometer(timeline, timeout, policy, start, expired, scope)

    private companion object {
        const val NO_ESTIMATE = -1.0
    }
}
