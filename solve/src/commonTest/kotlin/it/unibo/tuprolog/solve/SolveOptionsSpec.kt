package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.solve.SolveOptions.Companion.ALL_SOLUTIONS
import it.unibo.tuprolog.solve.SolveOptions.Companion.MAX_TIMEOUT
import it.unibo.tuprolog.utils.time.ChronometerFactory
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private class OptionsCase(
    val id: String,
    val options: SolveOptions,
    val lazy: Boolean,
    val limit: Int = ALL_SOLUTIONS,
    val timeout: Duration = MAX_TIMEOUT,
)

private val budget = 100.milliseconds

private val factoryCases =
    listOf(
        OptionsCase("all-lazily", SolveOptions.allLazily(), lazy = true),
        OptionsCase("some-lazily", SolveOptions.someLazily(3), lazy = true, limit = 3),
        OptionsCase("all-lazily-with-timeout", SolveOptions.allLazilyWithTimeout(budget), true, timeout = budget),
        OptionsCase("some-lazily-with-timeout", SolveOptions.someLazilyWithTimeout(3, budget), true, 3, budget),
        OptionsCase("all-eagerly", SolveOptions.allEagerly(), lazy = false),
        OptionsCase("some-eagerly", SolveOptions.someEagerly(3), lazy = false, limit = 3),
        OptionsCase("all-eagerly-with-timeout", SolveOptions.allEagerlyWithTimeout(budget), false, timeout = budget),
        OptionsCase("some-eagerly-with-timeout", SolveOptions.someEagerlyWithTimeout(3, budget), false, 3, budget),
        OptionsCase("default", SolveOptions.DEFAULT, lazy = true),
    )

class SolveOptionsSpec :
    FunSpec({
        withTests(nameFn = { "factories/${it.id}" }, factoryCases) { case ->
            val options = case.options
            options.isLazy shouldBe case.lazy
            options.isEager shouldBe !case.lazy
            options.limit shouldBe case.limit
            options.isLimited shouldBe (case.limit != ALL_SOLUTIONS)
            options.timeout shouldBe case.timeout
            options.chronometerFactory.shouldBeNull()
            options.customOptions shouldBe emptyMap()
        }

        test("max-timeout-is-infinite") {
            MAX_TIMEOUT shouldBe Duration.INFINITE
        }

        test("setters-return-modified-copies") {
            val original = SolveOptions.allLazily()
            val modified = original.setLazy(false).setLimit(2).setTimeout(10.milliseconds)
            modified shouldBe SolveOptions.someEagerlyWithTimeout(2, 10.milliseconds)
            original shouldBe SolveOptions.allLazily()
        }

        test("chronometer-factory/set-returns-a-modified-copy") {
            val factory = ChronometerFactory.simulated()
            val original = SolveOptions.allLazily()
            val modified = original.setChronometerFactory(factory)
            modified.chronometerFactory shouldBeSameInstanceAs factory
            original.chronometerFactory.shouldBeNull()
            modified.setChronometerFactory(null) shouldBe original
            SolveOptions.of(true, chronometerFactory = factory) shouldBe modified
        }

        test("chronometer-factory/creates-fresh-chronometers-with-the-timeout") {
            val timeline = Timeline.manual()
            val options =
                SolveOptions
                    .allLazilyWithTimeout(budget)
                    .setChronometerFactory(ChronometerFactory.simulated(timeline))
            val first = options.createChronometer()
            val second = options.createChronometer()
            first.timeout shouldBe budget
            first.timeline shouldBeSameInstanceAs timeline
            (first === second).shouldBeFalse()
        }

        test("chronometer-factory/defaults-to-the-library-default") {
            SolveOptions.DEFAULT.createChronometer().timeline shouldBeSameInstanceAs
                ChronometerFactory.default.create(Duration.INFINITE).timeline
        }

        test("custom-options/of") {
            val options = SolveOptions.of(true, MAX_TIMEOUT, ALL_SOLUTIONS, null, "a" to 1, "b" to 2)
            options.customOptions shouldBe mapOf("a" to 1, "b" to 2)
        }

        test("custom-options/set-replaces-add-merges") {
            val options = SolveOptions.allLazily().setOption("a", 1).addOption("b", 2)
            options.customOptions shouldBe mapOf("a" to 1, "b" to 2)
            options.addOptions(mapOf("a" to 3)).customOptions shouldBe mapOf("a" to 3, "b" to 2)
            options.setOptions(mapOf("c" to 4)).customOptions shouldBe mapOf("c" to 4)
            options.setOption("a", 5).customOptions shouldBe mapOf("a" to 5, "b" to 2)
        }

        test("invalid/zero-limit") {
            shouldThrow<IllegalArgumentException> { SolveOptions.someLazily(0) }
        }

        test("invalid/negative-timeout") {
            shouldThrow<IllegalArgumentException> { SolveOptions.allLazilyWithTimeout((-1).milliseconds) }
        }
    })
