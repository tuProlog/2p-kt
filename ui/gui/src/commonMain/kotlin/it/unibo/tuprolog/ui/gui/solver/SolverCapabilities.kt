package it.unibo.tuprolog.ui.gui.solver

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
