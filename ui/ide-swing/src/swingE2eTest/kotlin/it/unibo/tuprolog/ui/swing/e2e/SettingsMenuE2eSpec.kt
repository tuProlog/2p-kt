package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.fixture.FrameFixture
import kotlin.test.assertEquals

private const val ZOOM_DELTA = 8

/** Covers the "Settings" menu: restoring default settings, and deleting the persisted workspace. */
class SettingsMenuE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("Restore default settings resets a zoomed-in editor back to the default font size") {
            val editor = window.textBox("pageEditor")
            val defaultSize = editor.target().font.size
            // Swing components may only be mutated on the EDT once realized; FailOnThreadViolationRepaintManager
            // (installed by launchIdeSwing) enforces this and throws EdtViolationException on a direct write here.
            GuiActionRunner.execute {
                editor.target().font = editor.target().font.deriveFont((defaultSize + ZOOM_DELTA).toFloat())
            }

            window.menuItem("restoreDefaultSettingsMenuItem").click()

            window.awaitCondition("the editor to go back to the default font size") {
                editor.target().font.size == defaultSize
            }
            assertEquals(defaultSize, editor.target().font.size)
        }

        test("Delete persisted state asks for confirmation and can be cancelled") {
            window.menuItem("deletePersistedStateMenuItem").click()
            val dialog = window.optionPane()
            dialog.requireVisible()
            dialog.cancelButton().click()
            // Cancelling must not close the main window or otherwise disrupt it.
            window.requireVisible()
        }
    })
