package it.unibo.tuprolog.serialize

actual fun theorySerializer(mimeType: MimeType): TheorySerializer = JvmTheorySerializer(mimeType)

actual fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer = JvmTheoryDeserializer(mimeType)

actual fun theoryObjectifier(): TheoryObjectifier = JvmTheoryObjectifier()

actual fun theoryDeobjectifier(): TheoryDeobjectifier = JvmTheoryDeobjectifier()
