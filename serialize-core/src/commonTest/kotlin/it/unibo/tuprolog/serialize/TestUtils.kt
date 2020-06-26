package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.*

/**
 * Utility assertion method aimed at checking if a serializer correctly works
 */
fun <T : Term> Serializer<T>.assertSerializationWorks(expected: String, actual: T) {
    val expectedObj = parseAsObject(expected, mimeType)
    val actualObj = TermObjectifier.default.objectify(actual)
    kotlin.test.assertTrue("""
        |Expected:
        |   $expectedObj
        |got instead:
        |   $actualObj
        |
    """.trimMargin()) { deeplyEqual(expectedObj, actualObj) }
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
fun Serializer<Term>.assertTermSerializationWorks(expected: String, actualGenerator: Scope.() -> Term) {
    assertSerializationWorks(expected, Scope.empty().actualGenerator())
}

/**
 * Utility assertion method aimed at checking if a deserializer correctly works
 */
fun <T : Term> Deserializer<T>.assertDeserializationWorks(expected: T, actual: String) {
    kotlin.test.assertTrue { termsRepresentationsAreEqual(expected, deserialize(actual)) }
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
fun Deserializer<Term>.assertTermDeserializationWorks(actual: String, expectedGenerator: Scope.() -> Term) {
    assertDeserializationWorks(Scope.empty().expectedGenerator(), actual)
}

private fun termsRepresentationsAreEqual(t1: Term, t2: Term): Boolean {
    return when {
        t1 is Var && t2 is Var -> t1.name == t2.name
        t1 is Atom && t2 is Atom -> t1.value == t2.value
        t1 is Integer && t2 is Integer -> t1.value.compareTo(t2.value) == 0
        t1 is Real && t2 is Real -> t1.value.compareTo(t2.value) == 0
        t1 is Struct && t2 is Struct -> {
            t1.arity == t2.arity && t1.functor == t2.functor && (0 until t1.arity).all {
                termsRepresentationsAreEqual(
                    t1[it],
                    t2[it]
                )
            }
        }
        else -> false
    }
}