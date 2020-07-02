@file:JvmName("TheorySerialization")

package it.unibo.tuprolog.serialize

import kotlin.jvm.JvmName

expect fun theorySerializer(mimeType: MimeType): TheorySerializer

expect fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer

expect fun theoryObjectifier(): TheoryObjectifier

expect fun theoryDeobjectifier(): TheoryDeobjectifier