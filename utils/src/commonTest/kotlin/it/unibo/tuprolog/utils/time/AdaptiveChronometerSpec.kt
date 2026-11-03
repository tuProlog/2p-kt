package it.unibo.tuprolog.utils.time

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Drives an adaptive chronometer over a counting, manual timeline, advancing time by [leapCost] before each leap:
 * deterministic, so the exact clock readings can be asserted.
 */
private class AdaptiveFixture(
    timeout: Duration,
    policy: AdaptiveSamplingPolicy = AdaptiveSamplingPolicy(maxLeaps = 64),
) {
    val manual = Timeline.manual()
    val counting = Timeline.counting(manual)
    val chronometer = ChronometerFactory.adaptive(policy, counting).create(timeout)

    /** Performs [count] leaps costing [leapCost] each, returning the (1-based) indexes of the leaps reading the clock. */
    fun leaps(
        count: Int,
        leapCost: Duration,
    ): List<Int> =
        (1..count).filter {
            val before = counting.reads
            manual.advanceBy(leapCost)
            chronometer.leap()
            counting.reads > before
        }
}

class AdaptiveChronometerSpec :
    FunSpec({
        include(chronometerContract("adaptive") { ChronometerFactory.adaptive(timeline = it) })

        test("adaptive/never-reads-the-clock-without-a-timeout") {
            val fixture = AdaptiveFixture(Duration.INFINITE)
            fixture.leaps(100_000, 1.nanoseconds) shouldBe emptyList()
            fixture.chronometer.checkNow()
            fixture.counting.reads shouldBe 1L
        }

        test("adaptive/sampling-interval-grows-geometrically-up-to-the-maximum") {
            val fixture = AdaptiveFixture(1.hours)
            fixture.leaps(300, 1.nanoseconds) shouldBe listOf(1, 3, 7, 15, 31, 63, 127, 191, 255)
        }

        test("adaptive/sampling-interval-shrinks-when-leaps-slow-down") {
            val fixture = AdaptiveFixture(1.hours)
            fixture.leaps(300, 1.nanoseconds)
            // 100 µs per leap, in a 1 ms sampling period: the interval converges to 10 leaps
            val readings = fixture.leaps(2000, 100.microseconds)
            readings.zipWithNext { a, b -> b - a }.takeLast(10) shouldBe List(10) { 10 }
        }

        test("adaptive/sampling-window-is-a-fraction-of-the-remaining-budget") {
            // 1 µs per leap and a 1 ms timeout: at most 10% of the remaining budget, i.e. at most 100 leaps
            val fixture = AdaptiveFixture(1.milliseconds, AdaptiveSamplingPolicy())
            val readings = fixture.leaps(900, 1.microseconds)
            readings.zipWithNext { a, b -> b - a }.forEach { it shouldBeLessThanOrEqualTo 100 }
        }

        test("adaptive/expiration-is-never-premature-and-overshoot-is-bounded") {
            val fixture = AdaptiveFixture(10.milliseconds, AdaptiveSamplingPolicy())
            var leaps = 0
            while (!fixture.chronometer.isExpired) {
                fixture.manual.advanceBy(1.microseconds)
                val expired = fixture.chronometer.leap()
                leaps++
                if (expired) {
                    fixture.manual.now shouldBeGreaterThan 10.milliseconds
                }
            }
            fixture.chronometer.isExpired.shouldBeTrue()
            fixture.manual.now shouldBeLessThanOrEqualTo 10.milliseconds + 10.microseconds
            fixture.counting.reads shouldBeLessThanOrEqualTo leaps.toLong() / 50
        }

        test("adaptive/check-now-always-reads-the-clock") {
            val fixture = AdaptiveFixture(1.hours)
            fixture.leaps(300, 1.nanoseconds)
            val before = fixture.counting.reads
            repeat(5) { fixture.chronometer.checkNow() }
            fixture.counting.reads shouldBe before + 5
        }

        test("adaptive/restart-resets-the-sampling-interval") {
            val fixture = AdaptiveFixture(1.hours)
            fixture.leaps(300, 1.nanoseconds)
            fixture.chronometer.restart()
            fixture.leaps(10, 1.nanoseconds) shouldBe listOf(1, 3, 7)
        }
    })
