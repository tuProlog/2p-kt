package it.unibo.tuprolog.ui.swing.plp

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.plp.PlpFeatureKeys
import it.unibo.tuprolog.ui.swing.feature.SwingFeatureContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.swing.JLabel
import javax.swing.SwingUtilities
import kotlin.test.assertTrue

class PlpSwingFeatureRenderersSpec :
    FunSpec({
        val page =
            PageState(id = PageId("page"), title = "page", content = PageContent.DocumentReference(DocumentId("doc")))

        @Suppress("InjectDispatcher")
        val context = SwingFeatureContext(InertGuiController, CoroutineScope(Dispatchers.Unconfined))

        fun collectLabels(component: java.awt.Component): List<String> =
            when (component) {
                is JLabel -> listOf(component.text)
                is java.awt.Container -> component.components.flatMap(::collectLabels)
                else -> emptyList()
            }

        test("bdd renderer only shows a graph once a diagram becomes available") {
            SwingUtilities.invokeAndWait {
                val renderer = BddSwingFeatureRenderer()
                val panel = renderer.createComponent(context)

                renderer.render(panel, page, PageFeatureState())
                assertTrue(collectLabels(panel).any { it.contains("No binary decision diagram") })

                renderer.render(
                    panel,
                    page,
                    PageFeatureState(
                        mapOf(
                            PlpFeatureKeys.BDD_AVAILABLE to FeatureValue.BooleanValue(true),
                            PlpFeatureKeys.BDD_DOT to FeatureValue.Text("digraph { a -> b }"),
                        ),
                    ),
                )
                assertTrue(collectLabels(panel).any { it == "Binary decision diagram" })
            }
        }
    })
