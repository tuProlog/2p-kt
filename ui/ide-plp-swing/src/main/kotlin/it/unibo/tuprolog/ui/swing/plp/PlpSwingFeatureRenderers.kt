package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.swing.SwingFeatureRendererRegistry

fun plpSwingFeatureRenderers(
    bddGraphRenderer: SwingBddGraphRenderer = GraphvizSwingBddGraphRenderer,
): SwingFeatureRendererRegistry =
    SwingFeatureRendererRegistry(
        listOf(
            BddSwingFeatureRenderer(bddGraphRenderer),
        ),
    )
