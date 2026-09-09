package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.presentation.FeatureDescriptor
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

class GuiExtensionRegistry(
    extensions: Iterable<GuiExtension> = emptyList(),
) {
    private val materialisedExtensions: List<GuiExtension> = extensions.toList()
    private val extensionsById: Map<ExtensionId, GuiExtension> =
        materialisedExtensions.associateBy { it.id }.also { indexed ->
            require(indexed.size == materialisedExtensions.size) { "Duplicate extension id" }
        }

    val solverProfiles: List<SolverProfile> =
        extensionsById.values.flatMap { it.contributions.solverProfiles }.also(::requireDistinctSolverProfiles)
    val features: List<FeatureDescriptor> =
        extensionsById.values.flatMap { it.contributions.features }.also(::requireDistinctFeatures)
    val commands: List<CommandDescriptor> =
        extensionsById.values.flatMap { it.contributions.commands }.also(::requireDistinctCommands)

    fun find(id: ExtensionId): GuiExtension? = extensionsById[id]

    suspend fun handle(
        action: PageAction.ExtensionCommand,
        page: PageState,
    ): ExtensionCommandResult? {
        require(page.id == action.pageId) { "Extension command page does not match its page snapshot" }
        val handler = extensionsById[action.extensionId]?.contributions?.actionHandler ?: return null
        return handler.handle(
            ExtensionCommandContext(
                page = page,
                commandId = action.commandId,
                payload = action.payload,
            ),
        )
    }

    private companion object {
        fun requireDistinctSolverProfiles(values: List<SolverProfile>) {
            require(values.map { it.id }.distinct().size == values.size) {
                "Duplicate solver profile contributed by extensions"
            }
        }

        fun requireDistinctFeatures(values: List<FeatureDescriptor>) {
            require(values.map { it.id }.distinct().size == values.size) {
                "Duplicate semantic feature contributed by extensions"
            }
        }

        fun requireDistinctCommands(values: List<CommandDescriptor>) {
            require(values.map { it.id }.distinct().size == values.size) {
                "Duplicate command contributed by extensions"
            }
        }
    }
}
