// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.e2e

import org.assertj.swing.fixture.FrameFixture
import java.awt.event.KeyEvent
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Covers application startup and the core query-execution workflow through the real Swing UI.
 *
 * One function per scenario is the point of an E2E suite; splitting the class would only fragment it.
 */
@Suppress("TooManyFunctions")
class StartupAndQueryE2eTest {
    private lateinit var window: FrameFixture

    @BeforeTest
    fun setUp() {
        window = launchIdeSwing()
    }

    @AfterTest
    fun tearDown() {
        window.cleanUp()
    }

    @Test
    fun `application starts with one untitled page and an idle status`() {
        window.requireVisible()
        val titles = window.tabbedPane("editorTabs").tabTitles()
        assertTrue(titles.size == 1, "expected exactly one initial page tab, got ${titles.toList()}")
        // SwingIdeFrame appends " •" while a page has unread panels (e.g. Stdout/Warnings never visited
        // yet), so a brand-new page's title carries it too; strip it before checking the base name.
        val title = titles[0].removeSuffix(" •")
        assertTrue(title.startsWith("untitled-") && title.endsWith(".pl"), "unexpected title: ${titles[0]}")
        window.label("statusLabel").requireText("Idle")
        window.button("solveButton").requireText("Solve")
    }

    @Test
    fun `a query can be solved and shows its solution in the Solutions tab`() {
        window.textBox("queryField").setText("member(X, [1, 2]).")
        window.button("solveButton").click()

        window.awaitCondition("first solution to appear") {
            tree("solutionsTree").rowTexts().any { it.contains("X = 1") }
        }
        window.label("statusLabel").requireText("Solution available; more may exist")
    }

    @Test
    fun `solve again requests the next solution of the same query`() {
        window.textBox("queryField").setText("member(X, [1, 2]).")
        window.button("solveButton").click()
        window.awaitCondition("first solution") { tree("solutionsTree").rowTexts().any { it.contains("X = 1") } }

        window.button("solveButton").requireText("Next")
        window.button("solveButton").click()
        window.awaitCondition("second solution") { tree("solutionsTree").rowTexts().any { it.contains("X = 2") } }
    }

    @Test
    fun `solve all consumes every remaining solution and reports completion`() {
        window.textBox("queryField").setText("member(X, [1, 2, 3]).")
        window.button("solveAllButton").click()

        window.awaitCondition("every solution to appear", timeoutSeconds = 15) {
            val rows = tree("solutionsTree").rowTexts()
            listOf("X = 1", "X = 2", "X = 3").all { needle -> rows.any { it.contains(needle) } }
        }
        window.awaitCondition("resolution to report completion") {
            label("statusLabel").text() == "Resolution completed"
        }
    }

    @Test
    fun `a failing query is shown as no`() {
        window.textBox("queryField").setText("member(99, [1, 2]).")
        window.button("solveButton").click()

        window.awaitCondition("failure to appear") {
            tree("solutionsTree").rowTexts().any { it.contains("1. no") }
        }
    }

    @Test
    fun `an undefined predicate fails and reports a warning`() {
        window.textBox("queryField").setText("this_predicate_does_not_exist(x).")
        window.button("solveButton").click()

        window.awaitCondition("failure to appear") {
            tree("solutionsTree").rowTexts().any { it.contains("1. no") }
        }

        window.selectLowerTab("Warnings")
        window.awaitCondition("the warning text to mention the missing predicate") {
            textBox("warningsArea").text().contains("this_predicate_does_not_exist")
        }
    }

    @Test
    fun `a runtime error is reported as a halt in the Solutions tab`() {
        window.textBox("queryField").setText("X is 1 / 0.")
        window.button("solveButton").click()

        window.awaitCondition("halt to appear") {
            tree("solutionsTree").rowTexts().any { it.contains("halt:") }
        }
    }

    @Test
    fun `a query that outruns its timeout is reported as a timeout halt`() {
        window.textBox("timeoutField").setText("200ms")
        window.textBox("timeoutField").pressAndReleaseKeys(KeyEvent.VK_ENTER)
        window.textBox("queryField").setText("repeat, fail.")
        window.button("solveButton").click()

        window.awaitCondition("timeout halt to appear", timeoutSeconds = 15) {
            tree("solutionsTree").rowTexts().any { it.contains("timeout:") }
        }
    }

    @Test
    fun `stop cancels a long-running resolution`() {
        window.textBox("queryField").setText("repeat, fail.")
        window.button("solveButton").click()

        window.awaitCondition("resolution to start running") { label("statusLabel").text().startsWith("Computing") }
        window.button("stopButton").click()
        window.awaitCondition("resolution to be cancelled", timeoutSeconds = 15) {
            label("statusLabel").text() == "Resolution cancelled"
        }
    }

    @Test
    fun `reset clears the current resolution back to idle`() {
        window.textBox("queryField").setText("member(X, [1, 2]).")
        window.button("solveButton").click()
        window.awaitCondition("first solution") { tree("solutionsTree").rowTexts().any { it.contains("X = 1") } }

        window.button("resetButton").click()
        window.awaitCondition("status back to idle") { label("statusLabel").text() == "Idle" }
        assertTrue(window.tree("solutionsTree").target().rowCount == 0)
    }
}
