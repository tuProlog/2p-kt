package it.unibo.tuprolog.utils.time

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TimelineSpec :
    FunSpec({
        test("manual/starts-at-zero-and-moves-only-when-advanced") {
            val timeline = Timeline.manual()
            timeline.now shouldBe Duration.ZERO
            timeline.source.markNow()
            timeline.now shouldBe Duration.ZERO
            timeline.advanceBy(3.milliseconds)
            timeline.now shouldBe 3.milliseconds
        }

        test("manual/waiting-advances-time") {
            val timeline = Timeline.manual()
            timeline.waitFor(5.milliseconds)
            timeline.waitFor((-1).milliseconds)
            timeline.now shouldBe 5.milliseconds
        }

        test("manual/advancing-backwards-is-rejected") {
            shouldThrow<IllegalArgumentException> { Timeline.manual().advanceBy((-1).milliseconds) }
        }

        test("counting/counts-clock-reads-but-not-waits") {
            val manual = Timeline.manual()
            val counting = Timeline.counting(manual)
            repeat(3) { counting.source.markNow() }
            counting.waitFor(1.milliseconds)
            counting.reads shouldBe 3L
            manual.now shouldBe 1.milliseconds
        }

        test("system/waiting-lets-at-least-that-much-time-elapse") {
            val timeline = Timeline.system
            val start = timeline.source.markNow()
            timeline.waitFor(5.milliseconds)
            (timeline.source.markNow() - start) shouldBeGreaterThanOrEqualTo 5.milliseconds
        }
    })
