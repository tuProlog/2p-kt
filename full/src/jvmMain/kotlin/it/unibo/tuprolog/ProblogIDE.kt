package it.unibo.tuprolog

import it.unibo.tuprolog.ui.swing.plp.main as launchPlpSwingIde

/**
 * JVM entry point of the `:full` module's PLP Swing IDE re-export:
 * `java -cp <classpath> it.unibo.tuprolog.ProblogIDE`.
 *
 * Delegates to `:ide-plp-swing`'s own `Main` entry point (see [it.unibo.tuprolog.ui.swing.plp] `Main.kt`), i.e.
 * this is the very same PLP Swing IDE shipped standalone by the `:ide-plp-swing` module, just reachable from a
 * project that only depends on `:full` -- see [PrologCLI]'s KDoc for why `:full` re-exports every UI module's
 * entry point this way, under this package, instead of requiring a separate dependency per frontend.
 */
object ProblogIDE {
    @JvmStatic
    fun main(args: Array<String>) = launchPlpSwingIde(args)
}
