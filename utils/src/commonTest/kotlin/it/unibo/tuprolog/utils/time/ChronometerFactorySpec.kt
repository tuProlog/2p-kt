package it.unibo.tuprolog.utils.time

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ChronometerFactorySpec :
    FunSpec({
        test("factories-create-fresh-running-chronometers") {
            val factory = ChronometerFactory.direct(Timeline.manual())
            val first = factory.create(10.milliseconds)
            val second = factory.create(10.milliseconds)
            (first === second).shouldBeFalse()
            first.isExpired.shouldBeFalse()
        }

        test("children-of-a-parent-are-bounded-by-it") {
            val timeline = Timeline.manual()
            val parent = ChronometerFactory.direct(timeline).create(10.milliseconds)
            timeline.advanceBy(4.milliseconds)
            val children = ChronometerFactory.childrenOf(parent)
            children.create(2.milliseconds).timeout shouldBe 2.milliseconds
            children.create(Duration.INFINITE).timeout shouldBe 6.milliseconds
            children.create(Duration.INFINITE).timeline shouldBeSameInstanceAs timeline
        }

        test("default-uses-the-system-timeline") {
            ChronometerFactory.default.create(Duration.INFINITE).timeline shouldBeSameInstanceAs Timeline.system
        }
    })
