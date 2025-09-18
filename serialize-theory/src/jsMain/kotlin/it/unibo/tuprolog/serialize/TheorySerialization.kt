package it.unibo.tuprolog.serialize

actual fun theorySerializer(mimeType: MimeType): TheorySerializer = JsTheorySerializer(mimeType)

actual fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer = JsTheoryDeserializer(mimeType)

actual fun theoryObjectifier(): TheoryObjectifier = JsTheoryObjectifier()

actual fun theoryDeobjectifier(): TheoryDeobjectifier = JsTheoryDeobjectifier()
