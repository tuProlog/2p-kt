package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

/**
 * JS entry point of the 2P-Kt REPL CLI: builds [TuPrologCmd] with its `solve` subcommand
 * ([TuPrologSolveQuery]) and hands [args] to clikt for parsing and execution -- mirroring the JVM entry point
 * in `Main.kt`.
 *
 * Note that this platform's `-T`/`--theory` support is not yet functional: [isReadableFile] and
 * [loadTheoryFromFile] are not implemented on JS (both throw `NotImplementedError`).
 */
fun main(args: Array<String>) {
    TuPrologCmd().subcommands(TuPrologSolveQuery()).main(args)
}
