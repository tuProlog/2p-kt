package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.theory.Theory

expect fun isReadableFile(path: String): Boolean

expect fun loadTheoryFromFile(path: String): Theory