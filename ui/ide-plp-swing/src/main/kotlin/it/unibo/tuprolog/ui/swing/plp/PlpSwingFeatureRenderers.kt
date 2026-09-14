package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.swing.feature.SwingFeatureRendererRegistry

/** The [SwingFeatureRendererRegistry] `ide-plp-swing` wires into a plain `ide-swing` frame to add the BDD tab. */
fun plpSwingFeatureRenderers(
    bddGraphRenderer: SwingBddGraphRenderer = PlantUmlSwingBddGraphRenderer,
): SwingFeatureRendererRegistry =
    SwingFeatureRendererRegistry(
        listOf(
            BddSwingFeatureRenderer(bddGraphRenderer),
        ),
    )
