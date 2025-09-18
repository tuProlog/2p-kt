package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import java.io.File

actual fun isReadableFile(path: String): Boolean = File(path).let { it.isFile && it.exists() && it.canRead() }

actual fun loadTheoryFromFile(path: String): Theory =
    File(path).let {
        Theory.parse(it.readText())
    }
