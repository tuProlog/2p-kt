package it.unibo.tuprolog.testutils

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

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

        j.hasNext() shouldBe i.hasNext()
    }

    fun assertTermsAreEqual(
        expected: Term,
        actual: Term,
    ) {
        actual.isGround shouldBe expected.isGround
        if (expected.isGround) {
            withClue(
                """Comparing:
                    |   actual: $actual
                    |     type: ${actual::class}
                    | expected: $expected
                    |     type: ${expected::class}
                """.trimMargin(),
            ) { actual shouldBe expected }
        } else {
            when {
                expected is Var && actual is Var -> {
                    actual.name shouldBe expected.name
                }
                expected is Constant && actual is Constant -> {
                    actual shouldBe expected
                }
                expected is Struct && actual is Struct -> {
                    actual.functor shouldBe expected.functor
                    actual.arity shouldBe expected.arity
                    for (i in 0 until expected.arity) {
                        assertTermsAreEqual(expected[i], actual[i])
                    }
                }
            }
            actual.variables.toSet().size shouldBe expected.variables.toSet().size
        }
    }
}
