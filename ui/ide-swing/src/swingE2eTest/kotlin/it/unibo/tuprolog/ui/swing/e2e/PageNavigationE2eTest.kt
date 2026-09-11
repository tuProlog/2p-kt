// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.e2e

import org.assertj.swing.fixture.FrameFixture
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Covers creating, selecting, and closing pages, and that per-page state does not leak across tabs. */
class PageNavigationE2eTest {
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
    fun `File New adds and selects another untitled document page`() {
        window.menuItem("newMenuItem").click()

        window.awaitCondition("a second page tab to appear") {
            tabbedPane("editorTabs").tabTitles().size == 2
        }
        assertEquals(1, window.tabbedPane("editorTabs").target().selectedIndex)
    }

    @Test
    fun `File New scratch page adds a scratch-named page`() {
        window.menuItem("newScratchPageMenuItem").click()

        window.awaitCondition("a scratch page tab to appear") {
            tabbedPane("editorTabs").tabTitles().any { it.removeSuffix(" •").startsWith("scratch-") }
        }
    }

    @Test
    fun `each page keeps its own query text when switching tabs`() {
        window.textBox("queryField").enterText("first_page_query.")
        window.menuItem("newMenuItem").click()
        window.awaitCondition("second page selected") { tabbedPane("editorTabs").tabTitles().size == 2 }

        window.textBox("queryField").requireText("")
        window.textBox("queryField").enterText("second_page_query.")

        window.tabbedPane("editorTabs").selectTab(0)
        window.awaitCondition("first page's query to come back") {
            textBox("queryField").text() == "first_page_query."
        }

        window.tabbedPane("editorTabs").selectTab(1)
        window.awaitCondition("second page's query to come back") {
            textBox("queryField").text() == "second_page_query."
        }
    }

    @Test
    fun `Close page removes the selected tab`() {
        window.menuItem("newMenuItem").click()
        window.awaitCondition("second page selected") { tabbedPane("editorTabs").tabTitles().size == 2 }

        window.menuItem("closePageMenuItem").click()
        window.awaitCondition("back down to one page") { tabbedPane("editorTabs").tabTitles().size == 1 }
    }

    @Test
    fun `closing every page falls back to a disabled, page-less workspace`() {
        window.menuItem("closePageMenuItem").click()

        window.awaitCondition("no pages left") { tabbedPane("editorTabs").tabTitles().isEmpty() }
        window.label("statusLabel").requireText("No page")
        assertTrue(!window.textBox("queryField").target().isEnabled)
        assertTrue(!window.button("solveButton").target().isEnabled)
    }
}
