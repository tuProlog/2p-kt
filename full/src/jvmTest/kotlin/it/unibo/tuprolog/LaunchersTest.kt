package it.unibo.tuprolog

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
class LaunchersTest {
    @BeforeTest
    fun forceHeadless() {
        System.setProperty("java.awt.headless", "true")
    }

    @Test
    fun `PrologCLI launches the REPL entry point`() {
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
        assertEquals(null, exit)
        assertTrue("yes" in out.toString().lowercase())
    }

    @Test
    fun `PrologIDE launches the Swing IDE entry point`() {
        val error = assertFailsWith<IllegalStateException> { PrologIDE.main(emptyArray()) }
        assertEquals("Cannot show the Swing IDE in a headless environment", error.message)
    }

    @Test
    fun `ProblogIDE launches the PLP Swing IDE entry point`() {
        val error = assertFailsWith<IllegalStateException> { ProblogIDE.main(emptyArray()) }
        assertEquals("Cannot show the Swing IDE in a headless environment", error.message)
    }
}
