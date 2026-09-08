package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Serializer] of whole [Theory] instances, e.g. to persist a knowledge base to disk or send it
 * to another process (or tool, possibly written in another language) as JSON, YAML, or XML.
 * Internally, a [TheorySerializer] objectifies the [Theory] via [TheoryObjectifier] (which in
 * turn objectifies each [it.unibo.tuprolog.core.Clause] with [TermObjectifier] into a plain list)
 * and then writes that list in the requested [MimeType].
 *
 * Usage example:
 * ```kotlin
 * val serializer: TheorySerializer = TheorySerializer.of(MimeType.Json)
 * val theory: Theory = Theory.of(Unificator.default, Rule.of(Atom.of("foo"), Atom.of("bar")))
 * val json: String = serializer.serialize(theory) // [{"head":"foo","body":"bar"}]
 * ```
 *
 * @see TheoryDeserializer for the inverse operation.
 */
interface TheorySerializer : Serializer<Theory> {
    companion object {
        /** Returns the platform-default [TheorySerializer] for the given [mimeType]. */
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TheorySerializer = theorySerializer(mimeType)
    }
}
