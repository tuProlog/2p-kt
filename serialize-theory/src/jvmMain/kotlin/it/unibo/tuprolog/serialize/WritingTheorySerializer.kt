package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

/**
 * A JVM-only [TheorySerializer] that can also write a [Theory] directly to a `java.io.Writer`,
 * e.g. to persist a knowledge base to a file or network stream without building the whole
 * `String` in memory first.
 *
 * @see ReadingTheoryDeserializer for the JVM-only reader-based counterpart.
 */
interface WritingTheorySerializer :
    TheorySerializer,
    WritingSerializer<Theory> {
    companion object {
        /** Returns the default [WritingTheorySerializer] for the given [mimeType]. */
        @JvmStatic
        fun of(mimeType: MimeType): WritingTheorySerializer = JvmTheorySerializer(mimeType)
    }
}
