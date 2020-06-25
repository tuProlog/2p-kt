@file:JvmName("SerializationUtils")

package it.unibo.tuprolog.serialize

import kotlin.jvm.JvmName

expect fun termSerializer(mimeType: MimeType): TermSerializer

expect fun termDeserializer(mimeType: MimeType): TermDeserializer

expect fun termObjectifier(): TermObjectifier

expect fun termDeobjectifier(): TermDeobjectifier