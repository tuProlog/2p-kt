package it.unibo.tuprolog.utils.time

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class DirectChronometerSpec :
    FunSpec({
        include(chronometerContract("direct") { ChronometerFactory.direct(it) })

        test("direct/reads-the-clock-at-every-leap") {
            val timeline = Timeline.counting(Timeline.manual())
            val chronometer = ChronometerFactory.direct(timeline).create(1.hours)
            repeat(100) { chronometer.leap() }
            timeline.reads shouldBe 101L
        }

        test("direct/never-reads-the-clock-without-a-timeout") {
            val timeline = Timeline.counting(Timeline.manual())
            val chronometer = ChronometerFactory.direct(timeline).create(Duration.INFINITE)
            repeat(100) { chronometer.leap() }
            chronometer.checkNow()
            timeline.reads shouldBe 1L
        }
    })
