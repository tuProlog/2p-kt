// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.e2e

import org.assertj.swing.core.matcher.JButtonMatcher
import org.assertj.swing.fixture.FrameFixture
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Covers the app's user-facing row/column numbering: code areas use one-based lines/columns for the caret, and
 * every diagnostic or runtime error message shown to the user reports a one-based location too, even though
 * `parser-impl`'s own [it.unibo.tuprolog.parser.sources.SourcePosition] stays zero-based internally.
 *
 * "`" is used as the offending character throughout: it is not a layout, quote, digit, letter, bracket, or
 * graphic character in this grammar (see `IncrementalTokenScanner.isGraphicCharacter`), so it deterministically
 * triggers an `UnexpectedCharacterException` at a known, single-character position.
 */
class PositionIndexingE2eTest {
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
    fun `a syntax error on the second line is reported with a one-based line and column`() {
        window.textBox("pageEditor").setText("a.\n`")

        window.selectLowerTab("Diagnostics")
        window.awaitCondition("a diagnostic to be listed", timeoutSeconds = 15) {
            list("diagnosticsList").contents().isNotEmpty()
        }
        val entry = window.list("diagnosticsList").contents().single()
        assertTrue(entry.contains("line 2, column 1"), "expected a one-based location, got: $entry")
    }

    @Test
    fun `the caret status label starts and stays one-based as the caret moves`() {
        window.label("caretLabel").requireText("Line 1, column 1")

        window.textBox("pageEditor").setText("a.\nb.\nc.")
        window.menuItem("goToLineMenuItem").click()
        val dialog = window.dialog()
        dialog.textBox().setText("3")
        dialog.button(JButtonMatcher.withText("OK")).click()

        window.awaitCondition("the caret label to report line 3, column 1") {
            label("caretLabel").text() == "Line 3, column 1"
        }
    }

    @Test
    fun `a broken page fails to solve with a one-based location in the failure message`() {
        window.textBox("pageEditor").setText("a.\n`")
        window.textBox("queryField").setText("true.")
        window.button("solveButton").click()

        window.awaitCondition("resolution to fail", timeoutSeconds = 15) {
            label("statusLabel").text().startsWith("Resolution failed")
        }
        val message = window.label("statusLabel").text()
        assertTrue(message.contains("2:1"), "expected a one-based location in: $message")
    }
}
