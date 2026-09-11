package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.bdd.toDotString
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.binaryDecisionDiagram
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue

/** Converts a solver-agnostic PLP [Solution] into the semantic feature state a PLP-capable frontend renders. */
fun Solution.plpFeatureState(): Map<FeatureId, Map<String, FeatureValue>> =
    PlpSolutionDetails(
        probability = (this as? Solution.Yes)?.probability,
        bdd = binaryDecisionDiagram?.let { BddPresentation(it.toDotString(), toString()) },
    ).toFeatureStateReplacements()
