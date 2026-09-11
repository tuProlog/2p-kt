// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.e2e

import org.assertj.swing.core.matcher.JButtonMatcher
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JTextArea
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Covers every menu bar entry: File, Edit, Search, and Help.
 *
 * One function per menu item is the point of an E2E suite; splitting the class would only fragment it.
 */
@Suppress("TooManyFunctions")
class MenusE2eTest {
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
    fun `File Open shows a file chooser that can be cancelled`() {
        window.menuItem("openMenuItem").click()
        window.fileChooser().cancel()
        window.awaitCondition("still exactly one page, nothing opened") {
            tabbedPane("editorTabs").tabTitles().size == 1
        }
    }

    @Test
    fun `File Save on a never-saved page shows a save file chooser that can be cancelled`() {
        window.menuItem("saveMenuItem").click()
        window.fileChooser().cancel()
    }

    @Test
    fun `File Reload on an untitled page is safely rejected`() {
        window.menuItem("reloadMenuItem").click()
        // Untitled documents have no origin to reload from; the controller rejects the action with no dialog.
        window.label("statusLabel").requireText("Idle")
    }

    @Test
    fun `File properties shows the current page's metadata`() {
        window.menuItem("filePropertiesMenuItem").click()
        val info = window.optionPane().requireInformationMessage()
        assertTrue(
            info
                .target()
                .message
                .toString()
                .contains("Encoding: UTF-8"),
        )
        info.okButton().click()
    }

    @Test
    fun `File Quit closes the window when there is nothing unsaved`() {
        window.menuItem("quitMenuItem").click()
        window.awaitCondition("the window to close") { !target().isShowing }
    }

    @Test
    fun `File Close page removes the page`() {
        window.menuItem("closePageMenuItem").click()
        window.awaitCondition("no pages left") { tabbedPane("editorTabs").tabTitles().isEmpty() }
    }

    @Test
    fun `Edit Select all selects the focused field's text`() {
        window.textBox("queryField").enterText("member(X, [1, 2]).")
        window.menuItem("selectAllMenuItem").click()

        val queryField = window.textBox("queryField").target()
        assertTrue(queryField.selectedText == "member(X, [1, 2]).")
    }

    @Test
    fun `Search Find opens and can be dismissed`() {
        window.menuItem("findMenuItem").click()
        val dialog = window.dialog()
        dialog.requireVisible()
        dialog.button(JButtonMatcher.withText("Cancel")).click()
        dialog.requireNotVisible()
    }

    @Test
    fun `Search Replace opens and can be dismissed`() {
        window.menuItem("replaceMenuItem").click()
        val dialog = window.dialog()
        dialog.requireVisible()
        dialog.button(JButtonMatcher.withText("Cancel")).click()
        dialog.requireNotVisible()
    }

    @Test
    fun `Search Go to line moves the caret to the requested line`() {
        window.textBox("pageEditor").enterText("a.\nb.\nc.\nd.")

        window.menuItem("goToLineMenuItem").click()
        val dialog = window.dialog()
        dialog.textBox().setText("3")
        dialog.button(JButtonMatcher.withText("OK")).click()

        val editor = window.textBox("pageEditor").target() as JTextArea
        assertTrue(editor.getLineOfOffset(editor.caretPosition) == 2)
    }

    @Test
    fun `Help About shows the application's info dialog`() {
        window.menuItem("aboutMenuItem").click()
        val info = window.optionPane().requireInformationMessage()
        assertTrue(
            info
                .target()
                .message
                .toString()
                .contains("tuProlog IDE"),
        )
        info.okButton().click()
    }

    @Test
    fun `Help Report an issue includes a status report and can be closed`() {
        window.menuItem("reportIssueMenuItem").click()
        val optionPane = window.optionPane()
        assertTrue(optionPane.textBox("reportIssueReportArea").text().contains("tuProlog IDE"))
        optionPane.buttonWithText("Close").click()
    }
}
