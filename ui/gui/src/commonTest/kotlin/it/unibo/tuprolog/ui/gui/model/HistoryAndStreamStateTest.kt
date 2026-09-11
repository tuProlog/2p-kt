package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSource
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HistoryAndStreamStateTest {
    @Test
    fun queryHistorySkipsBlankAndConsecutiveDuplicateEntries() {
        val history =
            QueryHistoryState()
                .record("p(X).")
                .record("p(X).")
                .record(" ")
                .record("q(Y).")
        assertEquals(listOf("p(X).", "q(Y)."), history.entries)
    }

    @Test
    fun queryHistoryDropsTheOldestEntryPastCapacity() {
        val history = (1..5).fold(QueryHistoryState(capacity = 3)) { acc, i -> acc.record("q$i.") }
        assertEquals(listOf("q3.", "q4.", "q5."), history.entries)
    }

    @Test
    fun queryHistoryRejectsANonPositiveCapacity() {
        assertFailsWith<IllegalArgumentException> { QueryHistoryState(capacity = 0) }
    }

    @Test
    fun pageHistoryDropsTheOldestResolutionPastCapacity() {
        val entry = { q: String -> ResolutionHistoryEntry(q, emptyList(), ResolutionStatus.COMPLETED) }
        val history = (1..3).fold(PageHistoryState(capacity = 2)) { acc, i -> acc.record(entry("r$i")) }
        assertEquals(listOf("r2", "r3"), history.resolutions.map { it.query })
    }

    @Test
    fun textStreamTracksUnreadChangesUntilMarkedRead() {
        var stream = TextStreamState()
        assertFalse(stream.hasUnreadChanges)
        stream = stream.append("hello")
        assertTrue(stream.hasUnreadChanges)
        assertEquals("hello", stream.text)
        stream = stream.markRead()
        assertFalse(stream.hasUnreadChanges)
        // Appending nothing must not bump the revision or re-flag the stream as unread.
        assertEquals(stream, stream.append(""))
    }

    @Test
    fun warningStreamTracksUnreadChangesUntilMarkedRead() {
        var stream = WarningStreamState()
        stream = stream.append(WarningPresentation("careful"))
        assertTrue(stream.hasUnreadChanges)
        assertEquals(listOf(WarningPresentation("careful")), stream.values)
        assertFalse(stream.markRead().hasUnreadChanges)
    }

    @Test
    fun diagnosticStateReplacesOnlyItsOwnSourceLeavingOthersIntact() {
        val syntax = DiagnosticSource("syntax")
        val solver = DiagnosticSource("solver")
        var state =
            DiagnosticState()
                .replaceSource(syntax, listOf(Diagnostic(DiagnosticSeverity.ERROR, "bad syntax", source = syntax)))
                .replaceSource(solver, listOf(Diagnostic(DiagnosticSeverity.WARNING, "careful", source = solver)))
        assertEquals(2, state.values.size)
        assertTrue(state.hasUnreadChanges)

        state = state.replaceSource(syntax, emptyList())
        assertEquals(1, state.values.size)
        assertEquals(solver, state.values.single().source)

        assertFalse(state.markRead().hasUnreadChanges)
    }
}
