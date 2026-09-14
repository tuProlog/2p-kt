package it.unibo.tuprolog.ui.gui.identity

/** Identifies one resolution (a single run of a query to completion/cancellation) within a page's history. */
data class ResolutionSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ResolutionSessionId cannot be blank" }
    }

    override fun toString(): String = value
}
