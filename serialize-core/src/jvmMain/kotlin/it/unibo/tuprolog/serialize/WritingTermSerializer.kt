package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

/**
 * A JVM-only [TermSerializer] that can also write a [Term] directly to a `java.io.Writer`, e.g.
 * to persist a term to a file or network stream without building the whole `String` in memory
 * first.
 *
 * @see ReadingTermDeserializer for the JVM-only reader-based counterpart.
 */
interface WritingTermSerializer :
    TermSerializer,
    WritingSerializer<Term> {
    companion object {
        /** Returns the default [WritingTermSerializer] for the given [mimeType]. */
        @JvmStatic
        fun of(mimeType: MimeType): WritingTermSerializer = JvmTermSerializer(mimeType)
    }
}
