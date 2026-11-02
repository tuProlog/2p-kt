package it.unibo.tuprolog.ui.gui.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSource
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation

class HistoryAndStreamStateSpec :
    FunSpec({
        test("query-history-skips-blank-and-consecutive-duplicate-entries") {
            val history =
                QueryHistoryState()
                    .record("p(X).")
                    .record("p(X).")
                    .record(" ")
                    .record("q(Y).")
            history.entries shouldBe listOf("p(X).", "q(Y).")
        }

        test("query-history-drops-the-oldest-entry-past-capacity") {
            val history = (1..5).fold(QueryHistoryState(capacity = 3)) { acc, i -> acc.record("q$i.") }
            history.entries shouldBe listOf("q3.", "q4.", "q5.")
        }

        test("query-history-rejects-a-non-positive-capacity") {
            shouldThrow<IllegalArgumentException> { QueryHistoryState(capacity = 0) }
        }

        test("page-history-drops-the-oldest-resolution-past-capacity") {
            val entry = { q: String -> ResolutionHistoryEntry(q, emptyList(), ResolutionStatus.COMPLETED) }
            val history = (1..3).fold(PageHistoryState(capacity = 2)) { acc, i -> acc.record(entry("r$i")) }
            (history.resolutions.map { it.query }) shouldBe listOf("r2", "r3")
        }

        test("text-stream-tracks-unread-changes-until-marked-read") {
            var stream = TextStreamState()
            stream.hasUnreadChanges.shouldBeFalse()
            stream = stream.append("hello")
            stream.hasUnreadChanges.shouldBeTrue()
            stream.text shouldBe "hello"
            stream = stream.markRead()
            stream.hasUnreadChanges.shouldBeFalse()
            // Appending nothing must not bump the revision or re-flag the stream as unread.
            stream.append("") shouldBe stream
        }

        test("warning-stream-tracks-unread-changes-until-marked-read") {
            var stream = WarningStreamState()
            stream = stream.append(WarningPresentation("careful"))
            stream.hasUnreadChanges.shouldBeTrue()
            stream.values shouldBe listOf(WarningPresentation("careful"))
            stream.markRead().hasUnreadChanges.shouldBeFalse()
        }

        test("diagnostic-state-replaces-only-its-own-source-leaving-others-intact") {
            val syntax = DiagnosticSource("syntax")
            val solver = DiagnosticSource("solver")
            var state =
                DiagnosticState()
                    .replaceSource(syntax, listOf(Diagnostic(DiagnosticSeverity.ERROR, "bad syntax", source = syntax)))
                    .replaceSource(solver, listOf(Diagnostic(DiagnosticSeverity.WARNING, "careful", source = solver)))
            state.values.size shouldBe 2
            state.hasUnreadChanges.shouldBeTrue()

            state = state.replaceSource(syntax, emptyList())
            state.values.size shouldBe 1
            state.values.single().source shouldBe solver

            state.markRead().hasUnreadChanges.shouldBeFalse()
        }
    })
