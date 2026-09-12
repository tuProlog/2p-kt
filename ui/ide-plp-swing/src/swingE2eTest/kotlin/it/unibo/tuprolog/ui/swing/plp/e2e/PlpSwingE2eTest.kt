// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.plp.e2e

import org.assertj.swing.fixture.FrameFixture
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Covers the PLP-specific additions to the shared Swing IDE: its two extra inspector tabs and templates. */
class PlpSwingE2eTest {
    private lateinit var window: FrameFixture

    @BeforeTest
    fun setUp() {
        window = launchIdePlpSwing()
    }

    @AfterTest
    fun tearDown() {
        window.cleanUp()
    }

    @Test
    fun `the Probability and BDD tabs are registered alongside the standard ones`() {
        val titles = window.tabbedPane("lowerTabs").tabTitles().map { it.removeSuffix("*") }
        assertTrue(titles.contains("Probability"))
        assertTrue(titles.contains("BDD"))
    }

    @Test
    fun `the BDD tab is registered right next to Solutions, not at the end`() {
        val titles = window.tabbedPane("lowerTabs").tabTitles().map { it.removeSuffix("*") }
        assertEquals(0, titles.indexOf("Solutions"))
        assertTrue(titles.indexOf("BDD") < titles.indexOf("Stdin"))
    }

    @Test
    fun `Probability and BDD tabs are disabled until the page's first solve`() {
        // The solver session (and the capability set that drives feature-tab enablement) is only built as
        // part of starting a resolution -- a freshly-created, never-solved page legitimately has neither yet.
        val pane = window.tabbedPane("lowerTabs")
        val titles = pane.tabTitles().map { it.removeSuffix("*") }
        assertTrue(!pane.target().isEnabledAt(titles.indexOf("Probability")))
        assertTrue(!pane.target().isEnabledAt(titles.indexOf("BDD")))

        window.textBox("queryField").setText("true.")
        window.button("solveButton").click()
        window.awaitCondition("the trivial query to solve") {
            tree("solutionsTree").rowTexts().isNotEmpty()
        }

        assertTrue(pane.target().isEnabledAt(titles.indexOf("Probability")))
        assertTrue(pane.target().isEnabledAt(titles.indexOf("BDD")))
    }

    @Test
    fun `New from template opens the Bayesian alarm network theory`() {
        window.menuItem("newFromTemplateMenuItem.bayesian-alarm").click()

        window.awaitCondition("the template page to open") {
            tabbedPane("editorTabs").tabTitles().any { it.removeSuffix("*").startsWith("bayesian-alarm.pl") }
        }
        window.awaitCondition("its source to be loaded into the page editor") {
            textBox("pageEditor").text().contains("0.7::burglary.")
        }
    }

    @Test
    fun `solving a probabilistic query populates the Probability and BDD tabs`() {
        window.textBox("pageEditor").setText(
            "0.5::heads1.\n0.6::heads2.\ntwoHeads :- heads1, heads2.",
        )
        window.textBox("queryField").setText("twoHeads.")
        window.button("solveButton").click()

        window.awaitCondition("the probabilistic solution to appear", timeoutSeconds = 15) {
            tree("solutionsTree").rowTexts().any { it.contains("twoHeads") }
        }

        window.selectLowerTab("Probability")
        window.awaitCondition("the Probability tab to show the computed probability") {
            label("probabilityLabel").text() == "Probability: 30%"
        }

        window.selectLowerTab("BDD")
        window.awaitCondition("the BDD tab to render the diagram's DOT source") {
            textBox("bddDotTextArea").text().contains("heads1")
        }
    }
}
