package it.unibo.tuprolog.ui.gui.application

import it.unibo.tuprolog.ui.gui.controller.DefaultGuiController
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.extension.GuiExtensionRegistry
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.ApplicationMetadata
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.solver.ResolutionSchedulingPolicy
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Fluent DSL for assembling a [GuiApplicationConfiguration] - see [buildGuiApplication] for the usual entry
 * point, which builds a whole running [GuiApplication] from one of these in a single call. */
class GuiApplicationBuilder {
    private var metadata: ApplicationMetadata = ApplicationMetadata()
    private var defaultProfileId: SolverProfileId? = null
    private var defaultTimeout: Duration = 5.seconds
    private var defaultOptions: Map<String, String> = emptyMap()
    private var schedulingPolicy: ResolutionSchedulingPolicy = ResolutionSchedulingPolicy.PER_PAGE_CONCURRENT
    private val profiles: MutableList<SolverProfile> = mutableListOf()
    private val extensions: MutableList<GuiExtension> = mutableListOf()

    /** Sets the application's cosmetic metadata (defaults to [ApplicationMetadata]'s own defaults). */
    fun metadata(value: ApplicationMetadata) = apply { metadata = value }

    /** Registers [profile], directly (as opposed to one contributed by an [extension]); the first one
     * registered becomes the default unless [makeDefault] is set on a later one. */
    fun solverProfile(
        profile: SolverProfile,
        makeDefault: Boolean = false,
    ) = apply {
        profiles += profile
        if (makeDefault || defaultProfileId == null) {
            defaultProfileId = profile.id
        }
    }

    /** Explicitly picks the default solver profile new pages/workspaces start with. */
    fun defaultSolverProfile(id: SolverProfileId) = apply { defaultProfileId = id }

    /** Sets the workspace-wide default resolution timeout; must be positive. */
    fun defaultTimeout(value: Duration) =
        apply {
            require(value.isPositive()) { "Default timeout must be positive" }
            defaultTimeout = value
        }

    /** Sets the workspace-wide default solver options. */
    fun defaultOptions(values: Map<String, String>) = apply { defaultOptions = values.toMap() }

    /** Sets whether resolutions on different pages may run concurrently or pre-empt each other. */
    fun schedulingPolicy(value: ResolutionSchedulingPolicy) = apply { schedulingPolicy = value }

    /** Registers [value], contributing its own solver profiles/features/commands to the application. */
    fun extension(value: GuiExtension) = apply { extensions += value }

    /** Validates every accumulated setting (no duplicate profile/extension ids, at least one profile, a valid
     * default) and produces the immutable configuration. */
    fun buildConfiguration(): GuiApplicationConfiguration {
        val materialisedExtensions = extensions.toList()
        val extensionRegistry = GuiExtensionRegistry(materialisedExtensions)
        val allProfiles = profiles + extensionRegistry.solverProfiles
        require(allProfiles.isNotEmpty()) { "At least one solver profile must be installed" }
        require(allProfiles.map { it.id }.distinct().size == allProfiles.size) { "Duplicate solver profile id" }
        require(materialisedExtensions.map { it.id }.distinct().size == materialisedExtensions.size) {
            "Duplicate extension id"
        }
        val selectedDefault = defaultProfileId ?: allProfiles.first().id
        require(allProfiles.any { it.id == selectedDefault }) { "Unknown default solver profile: $selectedDefault" }
        return GuiApplicationConfiguration(
            metadata = metadata,
            workspace =
                WorkspaceConfiguration(
                    defaultSolverProfileId = selectedDefault,
                    defaultTimeout = defaultTimeout,
                    defaultOptions = defaultOptions,
                    schedulingPolicy = schedulingPolicy,
                ),
            // Extension profiles are supplied through the registry below and must not be duplicated here.
            solverProfiles = profiles.toList(),
            extensions = materialisedExtensions,
        )
    }
}

/** Builds a whole running [GuiApplication] in one call: applies [configure] to a fresh [GuiApplicationBuilder],
 * validates the result, and wires a [DefaultGuiController] for it under [parentScope]. */
fun buildGuiApplication(
    parentScope: CoroutineScope,
    configure: GuiApplicationBuilder.() -> Unit,
): GuiApplication {
    val configuration = GuiApplicationBuilder().apply(configure).buildConfiguration()
    val registry = GuiExtensionRegistry(configuration.extensions)
    return GuiApplication(
        DefaultGuiController(
            workspaceConfiguration = configuration.workspace,
            baseProfiles = configuration.solverProfiles,
            parentScope = parentScope,
            extensions = registry,
            metadata = configuration.metadata,
        ),
    )
}
