package it.unibo.tuprolog.ui.swing.e2e

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.swing.app.installedLookAndFeels
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem
import javax.swing.UIManager
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Covers the "Look and Feel" menu: one radio item per installed look-and-feel, switching the running UI. */
class LookAndFeelMenuE2eSpec :
    FunSpec({
        lateinit var window: FrameFixture

        beforeTest { window = launchIdeSwing() }

        afterTest { window.cleanUp() }

        test("every installed look-and-feel has a menu item, matching the currently active one") {
            window.openMenu(window.menuItemWithPath("Look and Feel").target() as JMenu)
            val expectedNames = installedLookAndFeels().map { it.name }
            expectedNames.forEach { name ->
                window.menuItem("lookAndFeelMenuItem.$name").requireVisible()
            }
            val active = installedLookAndFeels().first { it.className == UIManager.getLookAndFeel().javaClass.name }
            val activeItem = window.menuItem("lookAndFeelMenuItem.${active.name}").target() as JRadioButtonMenuItem
            assertTrue(activeItem.isSelected)
            window.robot().pressAndReleaseKey(java.awt.event.KeyEvent.VK_ESCAPE)
        }

        test("picking another look-and-feel switches the running UI to it") {
            val other =
                installedLookAndFeels().first { it.className != UIManager.getLookAndFeel().javaClass.name }

            window.clickMenuItem("lookAndFeelMenuItem.${other.name}")

            window.awaitCondition("the look-and-feel to switch to ${other.name}") {
                UIManager.getLookAndFeel().javaClass.name == other.className
            }
            assertEquals(other.className, UIManager.getLookAndFeel().javaClass.name)
        }
    })
