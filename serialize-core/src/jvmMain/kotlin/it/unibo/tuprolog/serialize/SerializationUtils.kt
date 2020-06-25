package it.unibo.tuprolog.serialize

actual fun termSerializer(mimeType: MimeType): TermSerializer {
    return JvmTermSerializer(mimeType)
}

actual fun termDeserializer(mimeType: MimeType): TermDeserializer {
    return JvmTermDeserializer(mimeType)
}
