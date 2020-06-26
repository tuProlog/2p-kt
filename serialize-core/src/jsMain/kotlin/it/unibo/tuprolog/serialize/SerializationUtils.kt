package it.unibo.tuprolog.serialize

actual fun termSerializer(mimeType: MimeType): TermSerializer {
    return JsTermSerializer(mimeType)
}

actual fun termDeserializer(mimeType: MimeType): TermDeserializer {
    return JsTermDeserializer(mimeType)
}