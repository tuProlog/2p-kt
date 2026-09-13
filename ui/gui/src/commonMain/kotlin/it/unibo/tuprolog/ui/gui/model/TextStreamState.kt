package it.unibo.tuprolog.ui.gui.model

/** An append-only text stream (stdout/stderr) with an unread-changes counter for a lower-tab badge. */
data class TextStreamState(
    val text: String = "",
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    fun append(value: String): TextStreamState =
        if (value.isEmpty()) this else copy(text = text + value, revision = revision + 1)

    fun markRead(): TextStreamState = copy(seenRevision = revision)
}
