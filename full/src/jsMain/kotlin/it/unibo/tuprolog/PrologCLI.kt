package it.unibo.tuprolog

/**
 * JS implementation: delegates to `it.unibo.tuprolog.ui.repl`'s own JS entry point (see its `Repl.kt`), which
 * builds [it.unibo.tuprolog.ui.repl.TuPrologCmd] with its `solve` subcommand and hands it [args] -- same
 * caveat as the `:repl` module's own JS build: `-T`/`--theory` support is not functional yet on this platform
 * (see `it.unibo.tuprolog.ui.repl.isReadableFile`/`loadTheoryFromFile`).
 *
 * Unlike the JVM target, this module's JS target is built as a library rather than an executable (see
 * `binaries.library()` in this module's `build.gradle.kts`), so [main] is not invoked automatically on load --
 * a JS/Node consumer has to call it explicitly, passing its own argument array.
 */
actual fun main(args: Array<String>) {
    it.unibo.tuprolog.ui.repl
        .main(args)
}
