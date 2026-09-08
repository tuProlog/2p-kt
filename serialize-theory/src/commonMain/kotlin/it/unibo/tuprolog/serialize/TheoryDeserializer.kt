package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Deserializer] of whole [Theory] instances, e.g. to reload a previously persisted knowledge
 * base from disk, or to receive one sent by another process (or tool) as JSON, YAML, or XML.
 * Internally, a [TheoryDeserializer] parses the input in the requested [MimeType] into a plain
 * list and then reconstructs the [Theory] from it via [TheoryDeobjectifier].
 *
 * Usage example:
 * ```kotlin
 * val deserializer: TheoryDeserializer = TheoryDeserializer.of(MimeType.Json)
 * val theory: Theory = deserializer.deserialize("""[{"head":"foo","body":"bar"}]""")
 * ```
 *
 * @see TheorySerializer for the inverse operation.
 */
interface TheoryDeserializer : Deserializer<Theory> {
    companion object {
        /** Returns the platform-default [TheoryDeserializer] for the given [mimeType]. */
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TheoryDeserializer = theoryDeserializer(mimeType)
    }
}
