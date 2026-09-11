package it.unibo.tuprolog

import it.unibo.tuprolog.ui.swing.main as launchSwingIde

/**
 * JVM entry point of the `:full` module's Swing IDE re-export: `java -cp <classpath> it.unibo.tuprolog.PrologIDE`.
 *
 * Delegates to `:ide-swing`'s own `Main` entry point (see [it.unibo.tuprolog.ui.swing] `Main.kt`), i.e. this is
 * the very same Swing IDE shipped standalone by the `:ide-swing` module, just reachable from a project that
 * only depends on `:full` -- see [PrologCLI]'s KDoc for why `:full` re-exports every UI module's entry point
 * this way, under this package, instead of requiring a separate dependency per frontend.
 */
object PrologIDE {
    @JvmStatic
    fun main(args: Array<String>) = launchSwingIde(args)
}
