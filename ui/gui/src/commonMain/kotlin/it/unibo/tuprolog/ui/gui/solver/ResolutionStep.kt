package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

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
