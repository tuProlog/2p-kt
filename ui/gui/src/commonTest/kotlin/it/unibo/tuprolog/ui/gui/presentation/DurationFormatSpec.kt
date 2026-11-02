package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private data class ParseCase(
    val id: String,
    val input: String,
    val expected: Duration?,
)

private val parseCases =
    listOf(
        ParseCase("none", "none", Duration.ZERO),
        ParseCase("none-ignoring-case", " NONE ", Duration.ZERO),
        ParseCase("zero", "0", Duration.ZERO),
        ParseCase("milliseconds", "500ms", 500.milliseconds),
        ParseCase("several-units", "1h 30m", 90.minutes),
        ParseCase("several-units-without-spaces", "1h30m", 90.minutes),
        ParseCase("units-ignoring-case", "2S", 2.seconds),
        ParseCase("surrounding-spaces", "  2s  ", 2.seconds),
        ParseCase("one-week-is-the-maximum", "1w", 7.days),
        ParseCase("days-and-hours", "6d 23h", 6.days + 23.hours),
        ParseCase("empty", "", null),
        ParseCase("not-a-duration", "soon", null),
        ParseCase("unknown-unit", "1y", null),
        ParseCase("missing-unit", "1h 30", null),
        ParseCase("negative", "-1s", null),
        ParseCase("repeated-unit", "1h 1h", null),
        ParseCase("more-than-a-week", "8d", null),
        ParseCase("a-week-and-a-bit", "1w 1ms", null),
        ParseCase("amount-overflowing-a-long", "99999999999999999999ms", null),
    )

private data class FormatCase(
    val id: String,
    val duration: Duration,
    val input: String,
    val label: String,
)

private val formatCases =
    listOf(
        FormatCase("zero", Duration.ZERO, "none", "Timeout: no limit"),
        FormatCase("milliseconds-only", 500.milliseconds, "500ms", "Timeout: 500 ms"),
        FormatCase("one-of-a-unit", 1.hours, "1h", "Timeout: 1 hour"),
        FormatCase("plural-and-mixed", 2.hours + 30.minutes, "2h 30m", "Timeout: 2 hours 30 minutes"),
        FormatCase("seconds-and-milliseconds", 1500.milliseconds, "1s 500ms", "Timeout: 1 second 500 ms"),
        FormatCase("a-week", 7.days, "1w", "Timeout: 1 week"),
        FormatCase("days", 2.days + 1.seconds, "2d 1s", "Timeout: 2 days 1 second"),
    )

class DurationFormatSpec :
    FunSpec({
        withTests(nameFn = { "parse/${it.id}" }, parseCases) { case ->
            parseDurationInput(case.input) shouldBe case.expected
        }

        withTests(nameFn = { "format/${it.id}" }, formatCases) { case ->
            formatDurationInput(case.duration) shouldBe case.input
        }

        withTests(nameFn = { "format/${it.id}/parses-back" }, formatCases) { case ->
            parseDurationInput(formatDurationInput(case.duration)) shouldBe case.duration
        }

        withTests(nameFn = { "label/${it.id}" }, formatCases) { case ->
            durationLabel(case.duration) shouldBe case.label
        }

        test("format/negative-is-no-limit") {
            formatDurationInput((-1).seconds) shouldBe "none"
        }
    })
