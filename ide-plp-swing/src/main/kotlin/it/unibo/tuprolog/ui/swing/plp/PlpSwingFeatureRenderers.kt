package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.swing.SwingFeatureRendererRegistry

fun plpSwingFeatureRenderers(
    bddGraphRenderer: SwingBddGraphRenderer = DotTextSwingBddGraphRenderer,
): SwingFeatureRendererRegistry =
    SwingFeatureRendererRegistry(
        listOf(
            ProbabilitySwingFeatureRenderer(),
            BddSwingFeatureRenderer(bddGraphRenderer),
        ),
    )
