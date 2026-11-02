package it.unibo.tuprolog.serialize

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import it.unibo.tuprolog.serialize.ObjectsUtils.deeplyEqual
import it.unibo.tuprolog.serialize.ObjectsUtils.parseAsObject
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.itemWiseEquals

fun <T : Theory> Serializer<T>.assertSerializationWorks(
    expected: String,
    actual: T,
) {
    val expectedObj = parseAsObject(expected, mimeType)
    // both what the objectifier builds and what the serializer actually writes
    val actualObjs = listOf(TheoryObjectifier.default.objectify(actual), parseAsObject(serialize(actual), mimeType))
    actualObjs.forEach { actualObj ->
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

/** Whether [t1] and [t2] have the same clauses, in the same order, up to the complete names of their variables. */
fun representationsAreEqual(
    t1: Theory,
    t2: Theory,
): Boolean =
    itemWiseEquals(t1, t2) { x, y ->
        x.equals(y, false)
    }
