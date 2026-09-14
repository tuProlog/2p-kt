package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import java.io.File

/** JVM implementation: `true` iff [path] is a regular, existing, readable [File]. */
actual fun isReadableFile(path: String): Boolean = File(path).let { it.isFile && it.exists() && it.canRead() }

/**
 * JVM implementation: reads [path]'s full content with [File.readText] and parses it via `:parser-theory`'s
 * `Theory.parse` extension.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException if the file's content is not a syntactically valid theory.
 */
actual fun loadTheoryFromFile(path: String): Theory =
    File(path).let {
        Theory.parse(it.readText())
    }
