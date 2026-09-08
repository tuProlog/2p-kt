package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Deserializer] of [Term]s, e.g. to reload a previously persisted fact or query solution from
 * disk, or to receive one sent by another process (or tool) as JSON, YAML, or XML. Internally, a
 * [TermDeserializer] typically parses the input in the requested [MimeType] into a plain object
 * and then reconstructs the [Term] from it via [TermDeobjectifier].
 *
 * Usage example:
 * ```kotlin
 * val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
 * val term: Term = deserializer.deserialize("""{"fun":"f","args":["hello",2]}""")
 * ```
 *
 * @see TermSerializer for the inverse operation.
 *
 * A JVM-only `ReadingTermDeserializer` variant reads directly from a `java.io.Reader`.
 */
interface TermDeserializer : Deserializer<Term> {
    companion object {
        /** Returns the platform-default [TermDeserializer] for the given [mimeType]. */
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TermDeserializer = termDeserializer(mimeType)
    }
}
