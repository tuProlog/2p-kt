package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

/** One observable outcome of advancing a `ResolutionCursor`: either another solution ([Yield]), exhaustion
 * ([End]), or an unrecoverable error ([Failed]) - always carrying whatever I/O [signals] the solver emitted
 * meanwhile. */
sealed interface ResolutionStep {
    /** Output produced (e.g. to stdout/stderr) while computing this step, in emission order. */
    val signals: List<SolverSignal>

    /** A solution was found; [hasMorePotentially] says whether requesting another step could yield more. */
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

    /** The resolution is exhausted: no further solutions exist. */
    data class End(
        override val signals: List<SolverSignal> = emptyList(),
    ) : ResolutionStep

    /** The resolution could not proceed, e.g. a parse or solver-internal error rather than a normal "no". */
    data class Failed(
        val message: String,
        val causeType: String? = null,
        override val signals: List<SolverSignal> = emptyList(),
    ) : ResolutionStep
}
