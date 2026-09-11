package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

data class ResolutionState(
    val status: ResolutionStatus = ResolutionStatus.IDLE,
    val id: ResolutionSessionId? = null,
    val query: String? = null,
    val solutions: List<SolutionPresentation> = emptyList(),
    val error: String? = null,
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
        require(seenRevision in 0..revision) { "seenRevision must be between zero and revision" }
    }

    val canSolve: Boolean
        get() =
            status in
                setOf(
                    ResolutionStatus.IDLE,
                    ResolutionStatus.COMPLETED,
                    ResolutionStatus.FAILED,
                    ResolutionStatus.CANCELLED,
                )

    val canContinue: Boolean
        get() = status == ResolutionStatus.AWAITING_CONTINUATION

    val canStop: Boolean
        get() = status == ResolutionStatus.RUNNING || status == ResolutionStatus.AWAITING_CONTINUATION

    val hasUnreadChanges: Boolean
        get() = revision > seenRevision

    fun markRead(): ResolutionState = copy(seenRevision = revision)
}
