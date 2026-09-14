package it.unibo.tuprolog.ui.gui.template

/** A ready-to-load theory example, offered to the user e.g. via a "New from template" menu. */
data class TheoryTemplate(
    val id: String,
    val displayName: String,
    val source: String,
    val description: String = "",
) {
    init {
        require(id.isNotBlank()) { "id cannot be blank" }
        require(displayName.isNotBlank()) { "displayName cannot be blank" }
    }
}
