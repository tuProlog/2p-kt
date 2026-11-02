package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import org.assertj.swing.fixture.FrameFixture

/** Covers creating, selecting, and closing pages, and that per-page state does not leak across tabs. */
class PageNavigationE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("File New adds and selects another untitled document page") {
            window.clickMenuItem("newMenuItem")

            window.awaitCondition("a second page tab to appear") {
                tabbedPane("editorTabs").tabTitles().size == 2
            }
            window.tabbedPane("editorTabs").target().selectedIndex shouldBe 1
        }

        test("File New scratch page adds a scratch-named page") {
            window.clickMenuItem("newScratchPageMenuItem")

            window.awaitCondition("a scratch page tab to appear") {
                tabbedPane("editorTabs").tabTitles().any { it.removeSuffix(" •").startsWith("scratch-") }
            }
        }

        test("each page keeps its own query text when switching tabs") {
            window.textBox("queryField").setText("first_page_query.")
            window.clickMenuItem("newMenuItem")
            window.awaitCondition("second page selected") { tabbedPane("editorTabs").tabTitles().size == 2 }

            window.textBox("queryField").requireText("")
            window.textBox("queryField").setText("second_page_query.")

            window.tabbedPane("editorTabs").selectTab(0)
            window.awaitCondition("first page's query to come back") {
                textBox("queryField").text() == "first_page_query."
            }

            window.tabbedPane("editorTabs").selectTab(1)
            window.awaitCondition("second page's query to come back") {
                textBox("queryField").text() == "second_page_query."
            }
        }

        test("Close page removes the selected tab") {
            window.clickMenuItem("newMenuItem")
            window.awaitCondition("second page selected") { tabbedPane("editorTabs").tabTitles().size == 2 }

            window.clickMenuItem("closePageMenuItem")
            window.awaitCondition("back down to one page") { tabbedPane("editorTabs").tabTitles().size == 1 }
        }

        test("closing every page falls back to a disabled, page-less workspace") {
            window.clickMenuItem("closePageMenuItem")

            window.awaitCondition("no pages left") { tabbedPane("editorTabs").tabTitles().isEmpty() }
            window.label("statusLabel").requireText("No page")
            window
                .textBox("queryField")
                .target()
                .isEnabled
                .shouldBeFalse()
            window
                .button("solveButton")
                .target()
                .isEnabled
                .shouldBeFalse()
        }
    })
