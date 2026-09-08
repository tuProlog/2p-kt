@file:JvmName("TheorySerialization")

package it.unibo.tuprolog.serialize

import kotlin.jvm.JvmName

/**
 * Creates the platform-default [TheorySerializer] for [mimeType]. Backs [TheorySerializer.of].
 *
 * The returned serializer supports [MimeType.Xml] on the JVM, but not on Kotlin/JS: on JS,
 * calling [TheorySerializer.serialize] with a serializer built for [MimeType.Xml] throws
 * [NotImplementedError].
 */
expect fun theorySerializer(mimeType: MimeType): TheorySerializer

/**
 * Creates the platform-default [TheoryDeserializer] for [mimeType]. Backs [TheoryDeserializer.of].
 *
 * The returned deserializer supports [MimeType.Xml] on the JVM, but not on Kotlin/JS: on JS,
 * calling [TheoryDeserializer.deserialize] with a deserializer built for [MimeType.Xml] throws
 * [NotImplementedError].
 */
expect fun theoryDeserializer(mimeType: MimeType): TheoryDeserializer

/** Creates the platform-default [TheoryObjectifier]. Backs [TheoryObjectifier.default]. */
expect fun theoryObjectifier(): TheoryObjectifier

/** Creates the platform-default [TheoryDeobjectifier]. Backs [TheoryDeobjectifier.default]. */
expect fun theoryDeobjectifier(): TheoryDeobjectifier
