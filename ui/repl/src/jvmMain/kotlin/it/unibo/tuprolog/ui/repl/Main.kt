@file:JvmName("Main")

package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

/**
 * JVM entry point of the 2P-Kt REPL CLI: builds [TuPrologCmd] with its `solve` subcommand
 * ([TuPrologSolveQuery]) and hands [args] to clikt for parsing and execution.
 *
 * This is the `mainClass` the `2p-repl-VERSION-redist.jar` fat jar is built with (see the module's `build`
 * task), so `java -jar 2p-repl-VERSION-redist.jar [OPTIONS] [solve QUERY [-n N]]` runs this function.
 */
fun main(args: Array<String>) {
    TuPrologCmd().subcommands(TuPrologSolveQuery()).main(args)
}
