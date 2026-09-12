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

private const val ZOOM_DELTA = 8

/** Covers the "Settings" menu: restoring default settings, and deleting the persisted workspace. */
class SettingsMenuE2eTest {
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
    fun `Restore default settings resets a zoomed-in editor back to the default font size`() {
        val editor = window.textBox("pageEditor")
        val defaultSize = editor.target().font.size
        editor.target().font = editor.target().font.deriveFont((defaultSize + ZOOM_DELTA).toFloat())

        window.menuItem("restoreDefaultSettingsMenuItem").click()

        window.awaitCondition("the editor to go back to the default font size") {
            editor.target().font.size == defaultSize
        }
        assertEquals(defaultSize, editor.target().font.size)
    }

    @Test
    fun `Delete persisted state asks for confirmation and can be cancelled`() {
        window.menuItem("deletePersistedStateMenuItem").click()
        val dialog = window.optionPane()
        dialog.requireVisible()
        dialog.cancelButton().click()
        // Cancelling must not close the main window or otherwise disrupt it.
        window.requireVisible()
    }
}
