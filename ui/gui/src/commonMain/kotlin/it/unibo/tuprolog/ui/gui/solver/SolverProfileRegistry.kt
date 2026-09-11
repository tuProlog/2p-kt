package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId

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
