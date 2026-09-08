@file:JvmName("PrologCLI")

package it.unibo.tuprolog

/**
 * JVM implementation: delegates to `it.unibo.tuprolog.ui.repl`'s own JVM entry point (see its `Main.kt`),
 * which builds [it.unibo.tuprolog.ui.repl.TuPrologCmd] with its `solve` subcommand and hands it [args] --
 * i.e. running this is equivalent to running the standalone `:repl` module's own JVM entry point.
 */
actual fun main(args: Array<String>) {
    it.unibo.tuprolog.ui.repl
        .main(args)
}
