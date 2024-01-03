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

val MimeType.objectMapper: ObjectMapper
    get() = objectMappers[this] ?: throw NotImplementedError("MIME type not supported: $this")
