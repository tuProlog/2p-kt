@file:JvmName("PrologCLI")

package it.unibo.tuprolog

actual fun main(args: Array<String>) {
    it.unibo.tuprolog.ui.repl
        .main(args)
}
