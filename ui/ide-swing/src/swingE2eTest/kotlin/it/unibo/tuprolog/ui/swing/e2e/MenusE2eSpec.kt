package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import org.assertj.swing.core.matcher.JButtonMatcher
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JTextArea
import kotlin.test.assertTrue

/** Covers every menu bar entry: File, Edit, Search, and Help. */
class MenusE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("File Open shows a file chooser that can be cancelled") {
            window.clickMenuItem("openMenuItem")
            window.fileChooser().cancel()
            window.awaitCondition("still exactly one page, nothing opened") {
                tabbedPane("editorTabs").tabTitles().size == 1
            }
        }

        test("File Save on a never-saved page shows a save file chooser that can be cancelled") {
            window.clickMenuItem("saveMenuItem")
            window.fileChooser().cancel()
        }

        test("File Reload on an untitled page is safely rejected") {
            window.clickMenuItem("reloadMenuItem")
            // Untitled documents have no origin to reload from; the controller rejects the action with no dialog.
            window.label("statusLabel").requireText("Idle")
        }

        test("File properties shows the current page's metadata") {
            window.clickMenuItem("filePropertiesMenuItem")
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

        test("File Quit closes the window when there is nothing unsaved") {
            window.clickMenuItem("quitMenuItem")
            window.awaitCondition("the window to close") { !target().isShowing }
        }

        test("File Close page removes the page") {
            window.clickMenuItem("closePageMenuItem")
            window.awaitCondition("no pages left") { tabbedPane("editorTabs").tabTitles().isEmpty() }
        }

        test("Edit Select all selects the focused field's text") {
            window.textBox("queryField").setText("member(X, [1, 2]).")
            // "Select all" acts on whatever component currently owns OS keyboard focus, so the field must
            // actually be focused first -- setText() alone never grants it real focus.
            window.textBox("queryField").focus()
            window.clickMenuItem("selectAllMenuItem")

            val queryField = window.textBox("queryField").target()
            assertTrue(queryField.selectedText == "member(X, [1, 2]).")
        }

        test("Search Find opens and can be dismissed") {
            window.clickMenuItem("findMenuItem")
            val dialog = window.dialog()
            dialog.requireVisible()
            dialog.button(JButtonMatcher.withText("Cancel")).click()
            dialog.requireNotVisible()
        }

        test("Search Replace opens and can be dismissed") {
            window.clickMenuItem("replaceMenuItem")
            val dialog = window.dialog()
            dialog.requireVisible()
            dialog.button(JButtonMatcher.withText("Cancel")).click()
            dialog.requireNotVisible()
        }

        test("Search Go to line moves the caret to the requested line") {
            window.textBox("pageEditor").setText("a.\nb.\nc.\nd.")

            window.clickMenuItem("goToLineMenuItem")
            val dialog = window.dialog()
            dialog.textBox().setText("3")
            dialog.button(JButtonMatcher.withText("OK")).click()

            val editor = window.textBox("pageEditor").target() as JTextArea
            assertTrue(editor.getLineOfOffset(editor.caretPosition) == 2)
        }

        test("Help About shows the application's info dialog") {
            window.clickMenuItem("aboutMenuItem")
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

        test("Help Report an issue includes a status report and can be closed") {
            window.clickMenuItem("reportIssueMenuItem")
            val optionPane = window.optionPane()
            assertTrue(optionPane.textBox("reportIssueReportArea").text().contains("tuProlog IDE"))
            optionPane.buttonWithText("Close").click()
        }
    })
