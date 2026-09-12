// Backtick, sentence-style test names are this project's established convention (see SwingIdeComponentsTest);
// detekt's standard main/test tasks already exempt @Test functions from FunctionNaming, but the EXPERIMENTAL
// type-resolution task for this custom source set does not.
@file:Suppress("FunctionNaming")

package it.unibo.tuprolog.ui.swing.e2e

import it.unibo.tuprolog.ui.swing.installedLookAndFeels
import org.assertj.swing.fixture.FrameFixture
import javax.swing.JRadioButtonMenuItem
import javax.swing.UIManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Covers the "Look and Feel" menu: one radio item per installed look-and-feel, switching the running UI. */
class LookAndFeelMenuE2eTest {
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
    fun `every installed look-and-feel has a menu item, matching the currently active one`() {
        val menu = window.menuItemWithPath("Look and Feel")
        menu.click()
        val expectedNames = installedLookAndFeels().map { it.name }
        expectedNames.forEach { name ->
            window.menuItem("lookAndFeelMenuItem.$name").requireVisible()
        }
        val active = installedLookAndFeels().first { it.className == UIManager.getLookAndFeel().javaClass.name }
        val activeItem = window.menuItem("lookAndFeelMenuItem.${active.name}").target() as JRadioButtonMenuItem
        assertTrue(activeItem.isSelected)
        window.robot().pressAndReleaseKey(java.awt.event.KeyEvent.VK_ESCAPE)
    }

    @Test
    fun `picking another look-and-feel switches the running UI to it`() {
        val other =
            installedLookAndFeels().first { it.className != UIManager.getLookAndFeel().javaClass.name }

        window.menuItemWithPath("Look and Feel").click()
        window.menuItem("lookAndFeelMenuItem.${other.name}").click()

        window.awaitCondition("the look-and-feel to switch to ${other.name}") {
            UIManager.getLookAndFeel().javaClass.name == other.className
        }
        assertEquals(other.className, UIManager.getLookAndFeel().javaClass.name)
    }
}
