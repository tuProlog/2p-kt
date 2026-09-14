package it.unibo.tuprolog.ui.gui.model

/** A page's bounded history of concluded resolutions - what its Solutions tree/panel renders. */
data class PageHistoryState(
    val resolutions: List<ResolutionHistoryEntry> = emptyList(),
    val capacity: Int = 100,
) {
    init {
        require(capacity > 0) { "capacity must be positive" }
    }

    /** Appends [entry], dropping the oldest entries beyond [capacity]. */
    fun record(entry: ResolutionHistoryEntry): PageHistoryState =
        copy(resolutions = (resolutions + entry).takeLast(capacity))
}
