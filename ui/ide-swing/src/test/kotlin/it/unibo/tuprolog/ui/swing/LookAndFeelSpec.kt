package it.unibo.tuprolog.ui.swing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.swing.app.applyLookAndFeel
import it.unibo.tuprolog.ui.swing.app.installedLookAndFeels
import javax.swing.UIManager

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
            installedLookAndFeels().isNotEmpty().shouldBeTrue()
        }

        test("applying a known look-and-feel switches to it and reports success") {
            val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

            applyLookAndFeel(target.name).shouldBeTrue()

            UIManager.getLookAndFeel().javaClass.name shouldBe target.className
        }

        test("applying an unknown look-and-feel name fails without changing anything") {
            applyLookAndFeel("not-a-real-look-and-feel").shouldBeFalse()

            UIManager.getLookAndFeel().javaClass.name shouldBe originalLookAndFeel
        }

        test("look-and-feel name matching is case-insensitive") {
            val target = installedLookAndFeels().first { it.className != originalLookAndFeel }

            applyLookAndFeel(target.name.uppercase()).shouldBeTrue()

            UIManager.getLookAndFeel().javaClass.name shouldBe target.className
        }
    })
