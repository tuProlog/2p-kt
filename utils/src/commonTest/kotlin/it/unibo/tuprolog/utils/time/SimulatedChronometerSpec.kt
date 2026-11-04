package it.unibo.tuprolog.utils.time

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SimulatedChronometerSpec :
    FunSpec({
        include(chronometerContract("simulated") { ChronometerFactory.simulated(it) })

        test("simulated/leap-driven-time-expires-after-a-known-number-of-leaps") {
            val timeline = Timeline.manual()
            val chronometer = ChronometerFactory.simulated(timeline, 1.milliseconds).create(20.milliseconds)
            val detectedAt = generateSequence(1) { it + 1 }.first { chronometer.leap() }
            detectedAt shouldBe 21
            timeline.now shouldBe 21.milliseconds
        }

        test("simulated/leap-driven-time-advances-without-a-timeout") {
            val timeline = Timeline.manual()
            val chronometer = ChronometerFactory.simulated(timeline, 1.milliseconds).create(Duration.INFINITE)
            repeat(1000) { chronometer.leap().shouldBeFalse() }
            timeline.now shouldBe 1000.milliseconds
        }

        test("simulated/child-scope-leaps-advance-the-shared-timeline") {
            val timeline = Timeline.manual()
            val parent = ChronometerFactory.simulated(timeline, 1.milliseconds).create(10.milliseconds)
            val child = parent.childScope()
            repeat(5) { child.leap() }
            parent.elapsed() shouldBe 5.milliseconds
        }
    })
