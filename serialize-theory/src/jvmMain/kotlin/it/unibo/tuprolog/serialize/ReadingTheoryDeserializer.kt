package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

/**
 * A JVM-only [TheoryDeserializer] that can also read a [Theory] directly from a
 * `java.io.Reader`, e.g. to load a knowledge base from a file or network stream without
 * buffering it into a `String` first.
 *
 * @see WritingTheorySerializer for the JVM-only writer-based counterpart.
 */
interface ReadingTheoryDeserializer :
    TheoryDeserializer,
    ReadingDeserializer<Theory> {
    companion object {
        /** Returns the default [ReadingTheoryDeserializer] for the given [mimeType]. */
        @JvmStatic
        fun of(mimeType: MimeType): ReadingTheoryDeserializer = JvmTheoryDeserializer(mimeType)
    }
}
