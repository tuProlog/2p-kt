package it.unibo.tuprolog.ui.gui.identity

/** Stable identity of an interactive page/session. A page and a document are distinct concepts. */
data class PageId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "PageId cannot be blank" }
    }

    override fun toString(): String = value
}
