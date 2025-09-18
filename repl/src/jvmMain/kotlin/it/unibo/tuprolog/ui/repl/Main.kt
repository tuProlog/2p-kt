@file:JvmName("Main")

package it.unibo.tuprolog.ui.repl

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    TuPrologCmd().subcommands(TuPrologSolveQuery()).main(args)
}
