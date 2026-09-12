package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlin.test.Test
import kotlin.test.assertEquals

class PersistedResolutionTest {
    @Test
    fun `a Yes resolution round-trips through JSON-ready DTOs, including bindings and metadata`() {
        val original =
            ResolutionHistoryEntry(
                query = "p(X).",
                solutions =
                    listOf(
                        SolutionPresentation.Yes(
                            query = "p(X).",
                            bindings = listOf(BindingPresentation("X", "1")),
                            solvedQuery = "p(1).",
                            metadata = mapOf("probability" to "0.5"),
                        ),
                    ),
                terminalStatus = ResolutionStatus.COMPLETED,
            )

        assertEquals(original, original.toPersisted().toResolutionHistoryEntry())
    }

    @Test
    fun `No and Halt resolutions round-trip too`() {
        val no = ResolutionHistoryEntry("q.", listOf(SolutionPresentation.No("q.")), ResolutionStatus.COMPLETED)
        assertEquals(no, no.toPersisted().toResolutionHistoryEntry())

        val halt =
            ResolutionHistoryEntry(
                query = "r.",
                solutions =
                    listOf(
                        SolutionPresentation.Halt(
                            query = "r.",
                            message = "boom",
                            logicStackTrace = listOf("at r/0"),
                            isTimeout = true,
                        ),
                    ),
                terminalStatus = ResolutionStatus.FAILED,
                error = "boom",
            )
        assertEquals(halt, halt.toPersisted().toResolutionHistoryEntry())
    }

    @Test
    fun `an unrecognized persisted status falls back to COMPLETED instead of crashing`() {
        val persisted = ResolutionHistoryEntry("q.", emptyList(), ResolutionStatus.COMPLETED).toPersisted()
        val corrupted = persisted.copy(terminalStatus = "NOT_A_REAL_STATUS")

        assertEquals(ResolutionStatus.COMPLETED, corrupted.toResolutionHistoryEntry().terminalStatus)
    }
}
