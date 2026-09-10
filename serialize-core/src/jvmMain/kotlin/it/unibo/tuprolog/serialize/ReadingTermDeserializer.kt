package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

/**
 * A JVM-only [TermDeserializer] that can also read a [Term] directly from a `java.io.Reader`,
 * e.g. to load a term from a file or network stream without buffering it into a `String` first.
 *
 * @see WritingTermSerializer for the JVM-only writer-based counterpart.
 */
interface ReadingTermDeserializer :
    TermDeserializer,
    ReadingDeserializer<Term> {
    companion object {
        /** Returns the default [ReadingTermDeserializer] for the given [mimeType]. */
        @JvmStatic
        fun of(mimeType: MimeType): ReadingTermDeserializer = JvmTermDeserializer(mimeType)
    }
}
