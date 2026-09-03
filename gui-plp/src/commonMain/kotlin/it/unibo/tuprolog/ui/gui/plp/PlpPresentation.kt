package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue

/** Toolkit-neutral probabilistic details attached to a successful solution. */
data class PlpSolutionDetails(
    val probability: Double?,
    val bdd: BddPresentation? = null,
) {
    init {
        require(probability == null || probability in 0.0..1.0) {
            "probability must be in [0, 1]"
        }
    }

    /**
     * Replaces both semantic PLP feature states for the page's latest yielded solution.
     * In particular, a solution without a BDD clears any BDD left by the previous solution.
     */
    fun toFeatureStateReplacements(): Map<FeatureId, Map<String, FeatureValue>> =
        mapOf(
            PlpGuiIds.SOLUTION_DETAILS to
                buildMap {
                    probability?.let { put(PlpFeatureKeys.PROBABILITY, FeatureValue.Number(it)) }
                },
            PlpGuiIds.BDD_INSPECTOR to
                buildMap {
                    put(PlpFeatureKeys.BDD_AVAILABLE, FeatureValue.BooleanValue(bdd != null))
                    bdd?.let { value ->
                        put(PlpFeatureKeys.BDD_DOT, FeatureValue.Text(value.dot))
                        value.title?.let { title -> put(PlpFeatureKeys.BDD_TITLE, FeatureValue.Text(title)) }
                    }
                },
        )
}

data class BddPresentation(
    val dot: String,
    val title: String? = null,
) {
    init {
        require(dot.isNotBlank()) { "BDD DOT representation cannot be blank" }
    }
}

object PlpFeatureKeys {
    const val PROBABILITY: String = "probability"
    const val BDD_AVAILABLE: String = "bdd.available"
    const val BDD_DOT: String = "bdd.dot"
    const val BDD_TITLE: String = "bdd.title"
}
