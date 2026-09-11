package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation

data class WarningStreamState(
    val values: List<WarningPresentation> = emptyList(),
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    fun append(value: WarningPresentation): WarningStreamState = copy(values = values + value, revision = revision + 1)

    fun markRead(): WarningStreamState = copy(seenRevision = revision)
}
