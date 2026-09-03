package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import kotlin.time.Duration

data class SolverCapabilities(
    val values: Set<String> = emptySet(),
) {
    operator fun contains(capability: String): Boolean = capability in values

    fun containsAll(capabilities: Set<String>): Boolean = values.containsAll(capabilities)

    companion object {
        val EMPTY: SolverCapabilities = SolverCapabilities()

        const val STATIC_KB_INSPECTION: String = "static-kb-inspection"
        const val DYNAMIC_KB_INSPECTION: String = "dynamic-kb-inspection"
        const val OPERATORS_INSPECTION: String = "operators-inspection"
        const val FLAGS_INSPECTION: String = "flags-inspection"
        const val LIBRARIES_INSPECTION: String = "libraries-inspection"
        const val INTERACTIVE_INPUT: String = "interactive-input"
        const val CANCELLATION: String = "cancellation"
        const val PROBABILISTIC_SOLUTIONS: String = "probabilistic-solutions"
        const val BDD_PRESENTATION: String = "bdd-presentation"
    }
}

data class SolverProfile(
    val id: SolverProfileId,
    val displayName: String,
    val capabilities: SolverCapabilities,
    val factory: SolverSessionFactory,
    val defaultOptions: Map<String, String> = emptyMap(),
)

class SolverProfileRegistry private constructor(
    private val profiles: Map<SolverProfileId, SolverProfile>,
) {
    constructor(profiles: Iterable<SolverProfile>) : this(
        profiles.toList().let { materialised ->
            require(materialised.isNotEmpty()) { "At least one solver profile is required" }
            materialised.associateBy { it.id }.also { indexed ->
                require(indexed.size == materialised.size) { "Duplicate solver profile id" }
            }
        },
    )

    fun find(id: SolverProfileId): SolverProfile? = profiles[id]

    fun require(id: SolverProfileId): SolverProfile = profiles[id] ?: error("Unknown solver profile: $id")

    fun all(): List<SolverProfile> = profiles.values.sortedBy { it.displayName }

    fun plus(additionalProfiles: Iterable<SolverProfile>): SolverProfileRegistry {
        val merged = profiles.toMutableMap()
        for (profile in additionalProfiles) {
            require(profile.id !in merged) { "Duplicate solver profile: ${profile.id}" }
            merged[profile.id] = profile
        }
        return SolverProfileRegistry(merged)
    }
}

data class SolverSessionCreationRequest(
    val pageId: PageId,
    val documentId: DocumentId?,
    val sourceText: String,
    val documentRevision: Long,
    val profileId: SolverProfileId,
    val options: Map<String, String>,
    val stdin: String,
)

fun interface SolverSessionFactory {
    suspend fun create(request: SolverSessionCreationRequest): SolverSession
}

data class ResolutionRequest(
    val pageId: PageId,
    val query: String,
    val timeout: Duration,
    val options: Map<String, String>,
    val stdin: String,
)

interface SolverSession {
    val id: SolverSessionId
    val capabilities: SolverCapabilities
    val snapshot: SolverInspectionSnapshot

    suspend fun openResolution(request: ResolutionRequest): ResolutionCursor

    /** Restores a pristine solver state. Implementations may rebuild internally. */
    suspend fun reset(): SolverInspectionSnapshot

    suspend fun close()
}

interface ResolutionCursor {
    /** Computes one observable resolution step. */
    suspend fun next(): ResolutionStep

    /** Requests cancellation of the underlying computation, where supported. */
    suspend fun cancel()
}

sealed interface SolverSignal {
    data class Stdout(
        val text: String,
    ) : SolverSignal

    data class Stderr(
        val text: String,
    ) : SolverSignal

    data class Warning(
        val warning: WarningPresentation,
    ) : SolverSignal

    data class Diagnostics(
        val diagnostics: List<Diagnostic>,
    ) : SolverSignal

    data class Inspection(
        val snapshot: SolverInspectionSnapshot,
    ) : SolverSignal
}

sealed interface ResolutionStep {
    val signals: List<SolverSignal>

    data class Yield(
        val solution: SolutionPresentation,
        val hasMorePotentially: Boolean,
        override val signals: List<SolverSignal> = emptyList(),
        /**
         * Full replacement values for page-scoped semantic extension features produced by this solution.
         * Replacing, instead of blindly merging, prevents data from a previous solution (for example a BDD)
         * from surviving when the new solution does not provide it.
         */
        val featureStateReplacements: Map<FeatureId, Map<String, FeatureValue>> = emptyMap(),
    ) : ResolutionStep

    data class End(
        override val signals: List<SolverSignal> = emptyList(),
    ) : ResolutionStep

    data class Failed(
        val message: String,
        val causeType: String? = null,
        override val signals: List<SolverSignal> = emptyList(),
    ) : ResolutionStep
}

enum class ResolutionSchedulingPolicy {
    /** Different pages may compute independently. */
    PER_PAGE_CONCURRENT,

    /** Starting a resolution cancels computations in every other page. */
    SINGLE_ACTIVE_RESOLUTION,
}
