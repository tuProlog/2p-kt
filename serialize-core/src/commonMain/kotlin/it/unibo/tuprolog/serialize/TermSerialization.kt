@file:JvmName("TermSerialization")

package it.unibo.tuprolog.serialize

import kotlin.jvm.JvmName

/**
 * Creates the platform-default [TermSerializer] for [mimeType]. Backs [TermSerializer.of].
 *
 * The returned serializer supports [MimeType.Xml] on the JVM, but not on Kotlin/JS: on JS,
 * calling [TermSerializer.serialize] with a serializer built for [MimeType.Xml] throws
 * [NotImplementedError].
 */
expect fun termSerializer(mimeType: MimeType): TermSerializer

/**
 * Creates the platform-default [TermDeserializer] for [mimeType]. Backs [TermDeserializer.of].
 *
 * The returned deserializer supports [MimeType.Xml] on the JVM, but not on Kotlin/JS: on JS,
 * calling [TermDeserializer.deserialize] with a deserializer built for [MimeType.Xml] throws
 * [NotImplementedError].
 */
expect fun termDeserializer(mimeType: MimeType): TermDeserializer

/** Creates the platform-default [TermObjectifier]. Backs [TermObjectifier.default]. */
expect fun termObjectifier(): TermObjectifier

/** Creates the platform-default [TermDeobjectifier]. Backs [TermDeobjectifier.default]. */
expect fun termDeobjectifier(): TermDeobjectifier
