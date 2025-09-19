package it.unibo.tuprolog.serialize

actual fun termSerializer(mimeType: MimeType): TermSerializer = JvmTermSerializer(mimeType)

actual fun termDeserializer(mimeType: MimeType): TermDeserializer = JvmTermDeserializer(mimeType)

actual fun termObjectifier(): TermObjectifier = JvmTermObjectifier()

actual fun termDeobjectifier(): TermDeobjectifier = JvmTermDeobjectifier()
