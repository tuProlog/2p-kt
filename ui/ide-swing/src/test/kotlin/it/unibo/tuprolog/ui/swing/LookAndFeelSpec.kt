package it.unibo.tuprolog.ui.swing

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.swing.app.applyLookAndFeel
import it.unibo.tuprolog.ui.swing.app.installedLookAndFeels
import javax.swing.UIManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookAndFeelSpec :
    FunSpec({
        lateinit var originalLookAndFeel: String

        fun captureOriginal() {
            originalLookAndFeel = UIManager.getLookAndFeel().javaClass.name
        }

        fun restoreOriginal() {
            UIManager.setLookAndFeel(originalLookAndFeel)
        }

        beforeTest { captureOriginal() }

        afterTest { restoreOriginal() }

        test("every installed look-and-feel is reported") {
            assertTrue(installedLookAndFeels().isNotEmpty())
        }

        test("applying a known look-and-feel switches to it and reports success") {
            val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

            assertTrue(applyLookAndFeel(target.name))

            assertEquals(target.className, UIManager.getLookAndFeel().javaClass.name)
        }

        test("applying an unknown look-and-feel name fails without changing anything") {
            assertFalse(applyLookAndFeel("not-a-real-look-and-feel"))

            assertEquals(originalLookAndFeel, UIManager.getLookAndFeel().javaClass.name)
        }

        test("look-and-feel name matching is case-insensitive") {
            val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

            assertTrue(applyLookAndFeel(target.name.uppercase()))

            assertEquals(target.className, UIManager.getLookAndFeel().javaClass.name)
        }
    })
