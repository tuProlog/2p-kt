package it.unibo.tuprolog.ui.swing

import javax.swing.UIManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookAndFeelTest {
    private lateinit var originalLookAndFeel: String

    @BeforeTest
    fun captureOriginal() {
        originalLookAndFeel = UIManager.getLookAndFeel().javaClass.name
    }

    @AfterTest
    fun restoreOriginal() {
        UIManager.setLookAndFeel(originalLookAndFeel)
    }

    @Test
    fun `every installed look-and-feel is reported`() {
        assertTrue(installedLookAndFeels().isNotEmpty())
    }

    @Test
    fun `applying a known look-and-feel switches to it and reports success`() {
        val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

        assertTrue(applyLookAndFeel(target.name))

        assertEquals(target.className, UIManager.getLookAndFeel().javaClass.name)
    }

    @Test
    fun `applying an unknown look-and-feel name fails without changing anything`() {
        assertFalse(applyLookAndFeel("not-a-real-look-and-feel"))

        assertEquals(originalLookAndFeel, UIManager.getLookAndFeel().javaClass.name)
    }

    @Test
    fun `look-and-feel name matching is case-insensitive`() {
        val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

        assertTrue(applyLookAndFeel(target.name.uppercase()))

        assertEquals(target.className, UIManager.getLookAndFeel().javaClass.name)
    }
}
