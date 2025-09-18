package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.serialize.ObjectsUtils.deeplyEqual
import it.unibo.tuprolog.serialize.ObjectsUtils.parseAsObject
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.itemWiseEquals

fun <T : Theory> Serializer<T>.assertSerializationWorks(
    expected: String,
    actual: T,
) {
    val expectedObj = parseAsObject(expected, mimeType)
    val actualObj = TheoryObjectifier.default.objectify(actual)
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

fun Serializer<Theory>.assertTermSerializationWorks(
    expected: String,
    actualGenerator: Scope.() -> Theory,
) {
    assertSerializationWorks(expected, Scope.empty().actualGenerator())
}

fun <T : Theory> Deserializer<T>.assertDeserializationWorks(
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
    ) { representationsAreEqual(expected, deserialized) }
}

fun Deserializer<Theory>.assertTermDeserializationWorks(
    actual: String,
    expectedGenerator: Scope.() -> Theory,
) {
    assertDeserializationWorks(Scope.empty().expectedGenerator(), actual)
}

private fun representationsAreEqual(
    t1: Theory,
    t2: Theory,
): Boolean =
    itemWiseEquals(t1, t2) { x, y ->
        x.equals(y, false)
    }
