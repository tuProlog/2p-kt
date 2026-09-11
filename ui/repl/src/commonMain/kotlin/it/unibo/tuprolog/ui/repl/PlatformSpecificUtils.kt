package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.theory.Theory

/**
 * Tells whether [path] denotes a file that exists and can be read on the current platform.
 *
 * Used by [TuPrologCmd] to check each `-T`/`--theory` argument before attempting to load it, so a missing
 * or unreadable file is silently skipped rather than raising a platform-specific I/O error.
 *
 * Implemented per-platform: the JVM implementation delegates to `java.io.File`; the JS implementation is not
 * yet provided (see [loadTheoryFromFile]).
 */
expect fun isReadableFile(path: String): Boolean

/**
 * Reads the file at [path] and parses its content as a Prolog [Theory], using the default operator set and
 * `:parser-theory`'s clause parser (see `it.unibo.tuprolog.theory.parsing.ClausesParser`).
 *
 * Called by [TuPrologCmd] for every `-T`/`--theory` file that [isReadableFile] deemed loadable.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException if the file's content is not a syntactically valid theory.
 *
 * Implemented per-platform: the JVM implementation reads the file via `java.io.File` and parses it with
 * `Theory.parse`; the JS implementation currently throws `NotImplementedError` (`TODO`) and is not yet functional.
 */
expect fun loadTheoryFromFile(path: String): Theory
