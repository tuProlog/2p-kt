package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.identity.ExtensionId

interface GuiExtension {
    val id: ExtensionId
    val contributions: GuiContributions
}
