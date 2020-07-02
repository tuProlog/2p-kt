package it.unibo.tuprolog.serialize

actual fun theorySerializer(mimeType: MimeType): TheorySerializer {
    return JsTheorySerializer(mimeType)
}

actual fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer {
    return JsTheoryDeserializer(mimeType)
}

actual fun theoryObjectifier(): TheoryObjectifier {
    return JsTheoryObjectifier()
}

actual fun theoryDeobjectifier(): TheoryDeobjectifier {
    return JsTheoryDeobjectifier()
}