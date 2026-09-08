package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Serializer] of [Term]s, e.g. to persist a knowledge base fact or a query solution to disk,
 * or to send it to another process (or another tool, possibly written in another language) as
 * JSON, YAML, or XML. Internally, a [TermSerializer] typically converts the [Term] into a plain
 * object via [TermObjectifier] and then writes that object in the requested [MimeType].
 *
 * Usage example:
 * ```kotlin
 * val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
 * val term: Term = Struct.of("f", Atom.of("hello"), Integer.of(2))
 * val json: String = serializer.serialize(term) // {"fun":"f","args":["hello",2]}
 * ```
 *
 * @see TermDeserializer for the inverse operation.
 *
 * A JVM-only `WritingTermSerializer` variant writes directly to a `java.io.Writer`.
 */
interface TermSerializer : Serializer<Term> {
    companion object {
        /** Returns the platform-default [TermSerializer] for the given [mimeType]. */
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TermSerializer = termSerializer(mimeType)
    }
}
