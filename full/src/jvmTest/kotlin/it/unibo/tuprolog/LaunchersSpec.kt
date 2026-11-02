package it.unibo.tuprolog

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Exercises every entry point `:full` re-exports (see [PrologCLI], [PrologIDE], [ProblogIDE]), proving each
 * one actually resolves and delegates to its underlying UI module's own entry point instead of just compiling.
 * This is the concrete regression these tests guard against: `:full` kept referencing
 * `it.unibo.tuprolog.ui.gui.Main`/`PLPMain` from the pre-migration JavaFX `:ide`/`:ide-plp` modules well after
 * those were replaced by `:ide-swing`/`:ide-plp-swing`, and nothing caught it until the build broke outright.
 *
 * The Swing-based entry points ([PrologIDE], [ProblogIDE]) are asserted against their own headless guard
 * (`SwingIdeApplication.show`'s `check(!GraphicsEnvironment.isHeadless())`) rather than actually opened, so
 * these tests are safe to run without a display; `java.awt.headless` is forced here rather than relying on
 * the runner's own environment, so the assertion holds locally too.
 */
class LaunchersSpec :
    FunSpec({
        fun forceHeadless() {
            System.setProperty("java.awt.headless", "true")
        }

        beforeTest { forceHeadless() }

        test("PrologCLI launches the REPL entry point") {
            val out = ByteArrayOutputStream()
            val exit =
                System.out.let { original ->
                    try {
                        System.setOut(PrintStream(out))
                        main(arrayOf("solve", "true."))
                        null
                    } catch (e: Throwable) {
                        e
                    } finally {
                        System.setOut(original)
                    }
                }
            exit shouldBe null
            ("yes" in out.toString().lowercase()).shouldBeTrue()
        }

        test("PrologIDE launches the Swing IDE entry point") {
            val error = shouldThrow<IllegalStateException> { PrologIDE.main(emptyArray()) }
            error.message shouldBe "Cannot show the Swing IDE in a headless environment"
        }

        test("ProblogIDE launches the PLP Swing IDE entry point") {
            val error = shouldThrow<IllegalStateException> { ProblogIDE.main(emptyArray()) }
            error.message shouldBe "Cannot show the Swing IDE in a headless environment"
        }
    })
