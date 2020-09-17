package it.unibo.tuprolog.serialize

actual fun theorySerializer(mimeType: MimeType): TheorySerializer {
    return JvmTheorySerializer(mimeType)
}

actual fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer {
    return JvmTheoryDeserializer(mimeType)
}

actual fun theoryObjectifier(): TheoryObjectifier {
    return JvmTheoryObjectifier()
}

actual fun theoryDeobjectifier(): TheoryDeobjectifier {
    return JvmTheoryDeobjectifier()
}
