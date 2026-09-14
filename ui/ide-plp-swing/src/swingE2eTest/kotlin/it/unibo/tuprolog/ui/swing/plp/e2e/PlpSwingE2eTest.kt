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

/** Covers the PLP-specific additions to the shared Swing IDE: its extra BDD inspector tab and templates. */
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
    fun `the BDD tab is registered alongside the standard ones`() {
        val titles = window.tabbedPane("lowerTabs").tabTitles().map { it.removeSuffix("*") }
        assertTrue(titles.contains("BDD"))
    }

    @Test
    fun `the BDD tab is registered right next to Solutions, not at the end`() {
        val titles = window.tabbedPane("lowerTabs").tabTitles().map { it.removeSuffix("*") }
        assertEquals(0, titles.indexOf("Solutions"))
        assertTrue(titles.indexOf("BDD") < titles.indexOf("Stdin"))
    }

    @Test
    fun `the BDD tab is disabled until the page's first solve`() {
        // The solver session (and the capability set that drives feature-tab enablement) is only built as
        // part of starting a resolution -- a freshly-created, never-solved page legitimately has neither yet.
        val pane = window.tabbedPane("lowerTabs")
        val titles = pane.tabTitles().map { it.removeSuffix("*") }
        assertTrue(!pane.target().isEnabledAt(titles.indexOf("BDD")))

        window.textBox("queryField").setText("true.")
        window.button("solveButton").click()
        window.awaitCondition("the trivial query to solve") {
            tree("solutionsTree").rowTexts().isNotEmpty()
        }

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
    fun `solving a probabilistic query shows the probability next to the solution and renders the BDD`() {
        window.textBox("pageEditor").setText(
            "0.5::heads1.\n0.6::heads2.\ntwoHeads :- heads1, heads2.",
        )
        window.textBox("queryField").setText("twoHeads.")
        window.button("solveButton").click()

        // No more separate Probability tab: the probability is an annotation right on the solution row.
        window.awaitCondition("the probabilistic solution and its probability to appear", timeoutSeconds = 15) {
            tree("solutionsTree").rowTexts().any { it.contains("twoHeads") && it.contains("p=30.0%") }
        }

        window.selectLowerTab("BDD")
        window.awaitCondition("the BDD tab to render an actual graph picture") {
            label("bddGraphImageLabel").target().icon != null
        }
    }
}
