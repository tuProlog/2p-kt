package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.PageState

data class ExtensionCommandContext(
    /** Immutable snapshot of the page that owns the command. */
    val page: PageState,
    val commandId: CommandId,
    val payload: Map<String, String>,
) {
    val pageId: PageId get() = page.id
}
