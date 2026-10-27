package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.solve.SolveOptions.Companion.ALL_SOLUTIONS
import it.unibo.tuprolog.solve.SolveOptions.Companion.MAX_TIMEOUT
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private class OptionsCase(
    val id: String,
    val options: SolveOptions,
    val lazy: Boolean,
    val limit: Int = ALL_SOLUTIONS,
    val timeout: TimeDuration = MAX_TIMEOUT,
)

private val factoryCases =
    listOf(
        OptionsCase("all-lazily", SolveOptions.allLazily(), lazy = true),
        OptionsCase("some-lazily", SolveOptions.someLazily(3), lazy = true, limit = 3),
        OptionsCase("all-lazily-with-timeout", SolveOptions.allLazilyWithTimeout(100), lazy = true, timeout = 100),
        OptionsCase("some-lazily-with-timeout", SolveOptions.someLazilyWithTimeout(3, 100), true, 3, 100),
        OptionsCase("all-eagerly", SolveOptions.allEagerly(), lazy = false),
        OptionsCase("some-eagerly", SolveOptions.someEagerly(3), lazy = false, limit = 3),
        OptionsCase("all-eagerly-with-timeout", SolveOptions.allEagerlyWithTimeout(100), lazy = false, timeout = 100),
        OptionsCase("some-eagerly-with-timeout", SolveOptions.someEagerlyWithTimeout(3, 100), false, 3, 100),
        OptionsCase("default", SolveOptions.DEFAULT, lazy = true),
    )

class SolveOptionsSpec :
    FunSpec({
        withTests(nameFn = { "factories/${it.id}" }, factoryCases) { case ->
            val options = case.options
            assertEquals(case.lazy, options.isLazy)
            assertEquals(!case.lazy, options.isEager)
            assertEquals(case.limit, options.limit)
            assertEquals(case.limit != ALL_SOLUTIONS, options.isLimited)
            assertEquals(case.timeout, options.timeout)
            assertEquals(emptyMap(), options.customOptions)
        }

        test("setters-return-modified-copies") {
            val original = SolveOptions.allLazily()
            val modified = original.setLazy(false).setLimit(2).setTimeout(10)
            assertEquals(SolveOptions.someEagerlyWithTimeout(2, 10), modified)
            assertEquals(SolveOptions.allLazily(), original)
        }

        test("custom-options/of") {
            val options = SolveOptions.of(true, MAX_TIMEOUT, ALL_SOLUTIONS, "a" to 1, "b" to 2)
            assertEquals(mapOf("a" to 1, "b" to 2), options.customOptions)
        }

        test("custom-options/set-replaces-add-merges") {
            val options = SolveOptions.allLazily().setOption("a", 1).addOption("b", 2)
            assertEquals(mapOf("a" to 1, "b" to 2), options.customOptions)
            assertEquals(mapOf("a" to 3, "b" to 2), options.addOptions(mapOf("a" to 3)).customOptions)
            assertEquals(mapOf("c" to 4), options.setOptions(mapOf("c" to 4)).customOptions)
            assertEquals(mapOf("a" to 5, "b" to 2), options.setOption("a", 5).customOptions)
        }

        test("invalid/zero-limit") {
            assertFailsWith<IllegalArgumentException> { SolveOptions.someLazily(0) }
        }

        test("invalid/negative-timeout") {
            assertFailsWith<IllegalArgumentException> { SolveOptions.allLazilyWithTimeout(-1) }
        }
    })
