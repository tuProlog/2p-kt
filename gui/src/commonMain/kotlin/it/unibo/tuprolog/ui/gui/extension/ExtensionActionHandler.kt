package it.unibo.tuprolog.ui.gui.extension

fun interface ExtensionActionHandler {
    suspend fun handle(context: ExtensionCommandContext): ExtensionCommandResult
}
