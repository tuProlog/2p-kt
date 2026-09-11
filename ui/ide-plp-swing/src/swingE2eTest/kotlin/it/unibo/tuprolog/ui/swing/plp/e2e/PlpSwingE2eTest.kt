// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.plp.e2e

import org.assertj.swing.fixture.FrameFixture
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
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
    fun `Probability and BDD tabs start in their empty placeholder state`() {
        window.selectLowerTab("Probability")
        window.label("probabilityLabel").requireText("No probabilistic solution")

        window.selectLowerTab("BDD")
        window.label("bddHeadingLabel").requireText("No binary decision diagram")
        assertTrue(!window.button("bddCopyDotButton").target().isEnabled)
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
        window.textBox("pageEditor").enterText(
            "0.5::heads1.\n0.6::heads2.\ntwoHeads :- heads1, heads2.",
        )
        window.textBox("queryField").enterText("twoHeads.")
        window.button("solveButton").click()

        window.awaitCondition("the probabilistic solution to appear", timeoutSeconds = 15) {
            tree("solutionsTree").rowTexts().any { it.contains("twoHeads") }
        }

        window.selectLowerTab("Probability")
        window.awaitCondition("the Probability tab to show the computed probability") {
            label("probabilityLabel").text() == "Probability: 30%"
        }

        window.selectLowerTab("BDD")
        window.awaitCondition("the BDD tab to show a diagram") {
            label("bddHeadingLabel").text() == "Binary decision diagram"
        }
        assertTrue(window.button("bddCopyDotButton").target().isEnabled)
    }
}
