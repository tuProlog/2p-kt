package it.unibo.tuprolog.utils.time

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

private const val MAX_LEAPS_TO_DETECT = 10_000

/**
 * Behaviour every [Chronometer] implementation must exhibit, on a [ManualTimeline] so that it is deterministic.
 * [factoryOn] builds the [ChronometerFactory] under test on a fresh timeline; its chronometers must not advance
 * time by themselves (simulated ones get a zero duration per leap). Leaf names are prefixed by [kind].
 */
fun chronometerContract(
    kind: String,
    factoryOn: (ManualTimeline) -> ChronometerFactory,
) = funSpec {
    fun fixture(timeout: Duration): Pair<ManualTimeline, Chronometer> {
        val timeline = Timeline.manual()
        return timeline to factoryOn(timeline).create(timeout)
    }

    test("$kind/starts-unexpired-with-the-full-budget") {
        val (_, chronometer) = fixture(10.milliseconds)
        chronometer.timeout shouldBe 10.milliseconds
        chronometer.isExpired.shouldBeFalse()
        chronometer.elapsed() shouldBe Duration.ZERO
        chronometer.remaining() shouldBe 10.milliseconds
    }

    test("$kind/before-the-deadline") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(4.milliseconds)
        chronometer.checkNow().shouldBeFalse()
        chronometer.elapsed() shouldBe 4.milliseconds
        chronometer.remaining() shouldBe 6.milliseconds
    }

    test("$kind/exactly-at-the-deadline-is-not-expired") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(10.milliseconds)
        chronometer.checkNow().shouldBeFalse()
        chronometer.remaining() shouldBe Duration.ZERO
        repeat(MAX_LEAPS_TO_DETECT) { chronometer.leap().shouldBeFalse() }
    }

    test("$kind/just-after-the-deadline-is-expired") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(10.milliseconds + 1.nanoseconds)
        chronometer.checkNow().shouldBeTrue()
        chronometer.isExpired.shouldBeTrue()
        chronometer.remaining() shouldBe Duration.ZERO
    }

    test("$kind/leaps-eventually-detect-expiration") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(11.milliseconds)
        val detectedAt = (1..MAX_LEAPS_TO_DETECT).firstOrNull { chronometer.leap() }
        (detectedAt != null).shouldBeTrue()
        chronometer.isExpired.shouldBeTrue()
    }

    test("$kind/expiration-is-sticky") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(11.milliseconds)
        chronometer.checkNow().shouldBeTrue()
        chronometer.leap().shouldBeTrue()
        chronometer.checkNow().shouldBeTrue()
        chronometer.isExpired.shouldBeTrue()
    }

    test("$kind/zero-timeout-expires-as-soon-as-time-elapses") {
        val (timeline, chronometer) = fixture(Duration.ZERO)
        chronometer.checkNow().shouldBeFalse()
        timeline.advanceBy(1.nanoseconds)
        chronometer.checkNow().shouldBeTrue()
    }

    test("$kind/infinite-timeout-never-expires") {
        val (timeline, chronometer) = fixture(Duration.INFINITE)
        timeline.advanceBy(10_000.days)
        chronometer.checkNow().shouldBeFalse()
        repeat(MAX_LEAPS_TO_DETECT) { chronometer.leap().shouldBeFalse() }
        chronometer.remaining() shouldBe Duration.INFINITE
        chronometer.elapsed() shouldBe 10_000.days
    }

    test("$kind/huge-timeout-does-not-overflow") {
        val huge = Long.MAX_VALUE.nanoseconds
        val (timeline, chronometer) = fixture(huge)
        timeline.advanceBy(10_000.days)
        chronometer.checkNow().shouldBeFalse()
        chronometer.remaining() shouldBe huge - 10_000.days
        chronometer.childScope(1.days).timeout shouldBe 1.days
    }

    test("$kind/negative-timeout-is-rejected") {
        shouldThrow<IllegalArgumentException> { factoryOn(Timeline.manual()).create((-1).milliseconds) }
    }

    test("$kind/observing-does-not-advance-time") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        repeat(3) {
            chronometer.elapsed()
            chronometer.remaining()
            chronometer.isExpired
            chronometer.checkNow()
        }
        timeline.now shouldBe Duration.ZERO
    }

    test("$kind/child-scope-is-bounded-by-the-parent-deadline") {
        val (timeline, parent) = fixture(10.milliseconds)
        timeline.advanceBy(4.milliseconds)
        parent.childScope().timeout shouldBe 6.milliseconds
        parent.childScope(2.milliseconds).timeout shouldBe 2.milliseconds
        parent.childScope(1.days).timeout shouldBe 6.milliseconds
    }

    test("$kind/child-scope-shares-the-timeline") {
        val (timeline, parent) = fixture(10.milliseconds)
        timeline.advanceBy(4.milliseconds)
        val child = parent.childScope()
        child.timeline shouldBe parent.timeline
        timeline.advanceBy(6.milliseconds + 1.nanoseconds)
        child.checkNow().shouldBeTrue()
        parent.checkNow().shouldBeTrue()
    }

    test("$kind/child-scope-of-an-unlimited-parent-has-the-requested-limit") {
        val (timeline, parent) = fixture(Duration.INFINITE)
        timeline.advanceBy(4.milliseconds)
        parent.childScope(3.milliseconds).timeout shouldBe 3.milliseconds
        parent.childScope().timeout shouldBe Duration.INFINITE
    }

    test("$kind/child-scope-of-an-expired-parent-is-expired") {
        val (timeline, parent) = fixture(10.milliseconds)
        timeline.advanceBy(11.milliseconds)
        val child = parent.childScope()
        child.isExpired.shouldBeTrue()
        child.timeout shouldBe Duration.ZERO
    }

    test("$kind/child-expiration-does-not-expire-the-parent") {
        val (timeline, parent) = fixture(10.milliseconds)
        val child = parent.childScope(2.milliseconds)
        timeline.advanceBy(3.milliseconds)
        child.checkNow().shouldBeTrue()
        parent.checkNow().shouldBeFalse()
    }

    test("$kind/sleep-within-the-budget") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        chronometer.sleep(3.milliseconds).shouldBeFalse()
        timeline.now shouldBe 3.milliseconds
    }

    test("$kind/sleep-beyond-the-budget-stops-at-the-deadline") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        chronometer.sleep(1000.milliseconds).shouldBeTrue()
        timeline.now shouldBe 10.milliseconds + 1.nanoseconds
        chronometer.isExpired.shouldBeTrue()
    }

    test("$kind/sleep-without-a-timeout") {
        val (timeline, chronometer) = fixture(Duration.INFINITE)
        chronometer.sleep(1000.milliseconds).shouldBeFalse()
        timeline.now shouldBe 1000.milliseconds
    }

    test("$kind/sleep-when-already-expired-does-not-wait") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(11.milliseconds)
        chronometer.sleep(5.milliseconds).shouldBeTrue()
        timeline.now shouldBe 11.milliseconds
    }

    test("$kind/negative-sleep-is-rejected") {
        val (_, chronometer) = fixture(10.milliseconds)
        shouldThrow<IllegalArgumentException> { chronometer.sleep((-1).milliseconds) }
    }

    test("$kind/restart-replenishes-the-budget") {
        val (timeline, chronometer) = fixture(10.milliseconds)
        timeline.advanceBy(11.milliseconds)
        chronometer.checkNow().shouldBeTrue()
        chronometer.restart()
        chronometer.isExpired.shouldBeFalse()
        chronometer.elapsed() shouldBe Duration.ZERO
        timeline.advanceBy(10.milliseconds)
        chronometer.checkNow().shouldBeFalse()
        timeline.advanceBy(1.nanoseconds)
        chronometer.checkNow().shouldBeTrue()
    }
}
