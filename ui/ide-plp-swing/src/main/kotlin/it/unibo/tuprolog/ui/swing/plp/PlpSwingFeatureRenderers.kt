package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.swing.feature.SwingFeatureRendererRegistry

fun plpSwingFeatureRenderers(
    bddGraphRenderer: SwingBddGraphRenderer = PlantUmlSwingBddGraphRenderer,
): SwingFeatureRendererRegistry =
    SwingFeatureRendererRegistry(
        listOf(
            BddSwingFeatureRenderer(bddGraphRenderer),
        ),
    )
