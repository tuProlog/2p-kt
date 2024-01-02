package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.serialize.ObjectsUtils.deeplyEqual
import it.unibo.tuprolog.serialize.ObjectsUtils.parseAsObject

/**
 * Utility assertion method aimed at checking if a serializer correctly works
 */
fun <T : Term> Serializer<T>.assertSerializationWorks(
    expected: String,
    actual: T,
) {
    val expectedObj = parseAsObject(expected, mimeType)
    val actualObj = TermObjectifier.default.objectify(actual)
    kotlin.test.assertTrue(
        """
        |Expected:
        |   $expectedObj
        |got instead:
        |   $actualObj
        |
        """.trimMargin(),
    ) { deeplyEqual(expectedObj, actualObj) }
}

/**
 * Utility assertion method aimed at checking if a serializer for [Term]s correctly works
 *
 * Usage example:
 * ```kotlin
 * val s: TermSerializer = // ...
 *
 * s.assertTermSerializationWorks("expected string") {
 *     // use utilities of it.unibo.tuprolog.core.Scope to build the to-be-serialized term
 * }
 * ```
 *
 * @see Scope https://pika-lab.gitlab.io/tuprolog/2p-in-kotlin/kotlindoc/it/unibo/tuprolog/core/scope/
 */
fun Serializer<Term>.assertTermSerializationWorks(
    expected: String,
    actualGenerator: Scope.() -> Term,
) {
    assertSerializationWorks(expected, Scope.empty().actualGenerator())
}

/**
 * Utility assertion method aimed at checking if a deserializer correctly works
 */
fun <T : Term> Deserializer<T>.assertDeserializationWorks(
    expected: T,
    actual: String,
) {
    val deserialized = deserialize(actual)
    kotlin.test.assertTrue(
        """
        |Expected:
        |   $expected
        |got:
        |   $deserialized
        |
        """.trimMargin(),
    ) { expected.equals(deserialized, false) }
}

/**
 * Utility assertion method aimed at checking if a deserializer for [Term]s correctly works
 *
 * Usage example:
 * ```kotlin
 * val d: TermDeserializer = // ...
 *
 * d.assertTermDeserializationWorks("to-be-deserialized string") {
 *     // use utilities of it.unibo.tuprolog.core.Scope to build the expected term
 * }
 * ```
 *
 * @see Scope https://pika-lab.gitlab.io/tuprolog/2p-in-kotlin/kotlindoc/it/unibo/tuprolog/core/scope/
 */
fun Deserializer<Term>.assertTermDeserializationWorks(
    actual: String,
    expectedGenerator: Scope.() -> Term,
) {
    assertDeserializationWorks(Scope.empty().expectedGenerator(), actual)
}
