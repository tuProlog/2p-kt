package it.unibo.tuprolog.ui.gui.application

import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.DefaultGuiController
import it.unibo.tuprolog.ui.gui.controller.GuiController
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

/**
 * Lifecycle wrapper around the toolkit-neutral controller.
 *
 * The hosting frontend owns the parent [CoroutineScope], so application shutdown cannot leak work into a process-global
 * scope. Solver profiles and semantic extensions are composed explicitly at the executable's composition root.
 */
class GuiApplication internal constructor(
    val controller: GuiController,
) {
    suspend fun start(): GuiApplication {
        controller.dispatch(ApplicationAction.Start)
        return this
    }

    suspend fun close() {
        controller.shutdown()
    }
}

data class GuiApplicationConfiguration(
    val metadata: ApplicationMetadata,
    val workspace: WorkspaceConfiguration,
    val solverProfiles: List<SolverProfile>,
    val extensions: List<GuiExtension>,
)

class GuiApplicationBuilder {
    private var metadata: ApplicationMetadata = ApplicationMetadata()
    private var defaultProfileId: SolverProfileId? = null
    private var defaultTimeout: Duration = 5.seconds
    private var defaultOptions: Map<String, String> = emptyMap()
    private var schedulingPolicy: ResolutionSchedulingPolicy = ResolutionSchedulingPolicy.PER_PAGE_CONCURRENT
    private val profiles: MutableList<SolverProfile> = mutableListOf()
    private val extensions: MutableList<GuiExtension> = mutableListOf()

    fun metadata(value: ApplicationMetadata) = apply { metadata = value }

    fun solverProfile(
        profile: SolverProfile,
        makeDefault: Boolean = false,
    ) = apply {
        profiles += profile
        if (makeDefault || defaultProfileId == null) {
            defaultProfileId = profile.id
        }
    }

    fun defaultSolverProfile(id: SolverProfileId) = apply { defaultProfileId = id }

    fun defaultTimeout(value: Duration) =
        apply {
            require(value.isPositive()) { "Default timeout must be positive" }
            defaultTimeout = value
        }

    fun defaultOptions(values: Map<String, String>) = apply { defaultOptions = values.toMap() }

    fun schedulingPolicy(value: ResolutionSchedulingPolicy) = apply { schedulingPolicy = value }

    fun extension(value: GuiExtension) = apply { extensions += value }

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
