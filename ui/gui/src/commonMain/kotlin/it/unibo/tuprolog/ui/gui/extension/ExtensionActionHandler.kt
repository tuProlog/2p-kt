package it.unibo.tuprolog.ui.gui.extension

/** Runs an extension's [CommandDescriptor]s: implementors interpret the command id in [ExtensionCommandContext]
 * and return whatever feature/event/effect updates that command should produce. */
fun interface ExtensionActionHandler {
    /** Handles one invocation of any command the owning extension contributed, dispatched via [context]. */
    suspend fun handle(context: ExtensionCommandContext): ExtensionCommandResult
}
