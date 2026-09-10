@file:JvmName("MimeTypeExtensions")

package it.unibo.tuprolog.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

private val objectMappers =
    mapOf(
        MimeType.Json to ObjectMapper(),
        MimeType.Yaml to YAMLMapper(),
        MimeType.Xml to XmlMapper(),
    )

/**
 * The shared Jackson `ObjectMapper` used, on the JVM, to read/write plain object trees in this
 * [MimeType]: a plain [ObjectMapper] for [MimeType.Json], a `YAMLMapper` for [MimeType.Yaml], and
 * an `XmlMapper` for [MimeType.Xml]. One mapper instance is reused for each [MimeType].
 *
 * @throws NotImplementedError if this [MimeType] has no known Jackson mapper (currently
 * impossible, since a mapper is registered for every existing [MimeType] subtype).
 */
val MimeType.objectMapper: ObjectMapper
    get() = objectMappers[this] ?: throw NotImplementedError("MIME type not supported: $this")
