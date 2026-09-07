package it.unibo.tuprolog.serialize

import kotlin.js.JsName

/**
 * A textual data format that a [Serializer] can produce and a [Deserializer] can consume,
 * identified by its [type] and [subType], e.g. `application/json`.
 *
 * Only the formats actually supported by this platform's [Serializer]/[Deserializer]
 * implementations are provided as singletons below; notably, [Xml] is not supported on the
 * Kotlin/JS target (attempting to serialize/deserialize it there throws [NotImplementedError]).
 */
sealed class MimeType(
    /** The top-level media type, e.g. `"application"`. */
    @JsName("type")
    val type: String,
    /** The media sub-type, e.g. `"json"`. */
    @JsName("subType")
    val subType: String,
) {
    /** The `application/json` MIME type. */
    object Json : MimeType("application", "json")

    /** The `application/yaml` MIME type. */
    object Yaml : MimeType("application", "yaml")

    /** The `application/xml` MIME type. Not supported on Kotlin/JS. */
    object Xml : MimeType("application", "xml")
}
