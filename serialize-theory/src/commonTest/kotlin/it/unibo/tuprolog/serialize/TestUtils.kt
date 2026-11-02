package it.unibo.tuprolog.serialize

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
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
    withClue(
        """
        |Expected:
        |   $expectedObj
        |got instead:
        |   $actualObj
        |
        """.trimMargin(),
    ) { deeplyEqual(expectedObj, actualObj).shouldBeTrue() }
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
    withClue(
        """
        |Expected:
        |   $expected
        |got:
        |   $deserialized
        |
        """.trimMargin(),
    ) { representationsAreEqual(expected, deserialized).shouldBeTrue() }
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
