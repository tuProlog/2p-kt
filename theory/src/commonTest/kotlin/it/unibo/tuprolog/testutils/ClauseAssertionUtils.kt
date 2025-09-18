package it.unibo.tuprolog.testutils

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals

internal object ClauseAssertionUtils {
    fun assertClausesHaveSameLengthAndContent(
        a: Sequence<Clause>,
        b: Sequence<Clause>,
    ) = assertClausesHaveSameLengthAndContent(a.asIterable(), b.asIterable())

    fun assertClausesHaveSameLengthAndContent(
        a: Iterable<Clause>,
        b: Iterable<Clause>,
    ) {
        val i = a.iterator()
        val j = b.iterator()

        while (i.hasNext() && j.hasNext()) {
            assertTermsAreEqual(i.next(), j.next())
        }

        assertEquals(i.hasNext(), j.hasNext())
    }

    fun assertTermsAreEqual(
        expected: Term,
        actual: Term,
    ) {
        assertEquals(expected.isGround, actual.isGround)
        if (expected.isGround) {
            assertEquals(
                expected,
                actual,
                message =
                    """Comparing:
                    |   actual: $actual
                    |     type: ${actual::class}
                    | expected: $expected
                    |     type: ${expected::class}
                    """.trimMargin(),
            )
        } else {
            when {
                expected is Var && actual is Var -> {
                    assertEquals(expected.name, actual.name)
                }
                expected is Constant && actual is Constant -> {
                    assertEquals(expected, actual)
                }
                expected is Struct && actual is Struct -> {
                    assertEquals(expected.functor, actual.functor)
                    assertEquals(expected.arity, actual.arity)
                    for (i in 0 until expected.arity) {
                        assertTermsAreEqual(expected[i], actual[i])
                    }
                }
            }
            assertEquals(
                expected.variables.toSet().size,
                actual.variables.toSet().size,
            )
        }
    }
}
