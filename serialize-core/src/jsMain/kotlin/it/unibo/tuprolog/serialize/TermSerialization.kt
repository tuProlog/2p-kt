package it.unibo.tuprolog.serialize

actual fun termSerializer(mimeType: MimeType): TermSerializer = JsTermSerializer(mimeType)

actual fun termDeserializer(mimeType: MimeType): TermDeserializer = JsTermDeserializer(mimeType)

actual fun termObjectifier(): TermObjectifier = JsTermObjectifier()

actual fun termDeobjectifier(): TermDeobjectifier = JsTermDeobjectifier()
