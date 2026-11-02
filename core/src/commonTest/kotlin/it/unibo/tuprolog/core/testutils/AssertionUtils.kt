package it.unibo.tuprolog.core.testutils

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.Term

/**
 * Utils singleton to test equality
 *
 * @author Enrico
 */
internal object AssertionUtils {
    /** Utility extension function to drop last element of a List */
    fun <T> List<T>.dropLast(): List<T> = this.dropLast(1)

    /** Utility extension function to drop first element of a List */
    fun <T> List<T>.dropFirst(): List<T> = this.drop(1)

    /** Asserts all boolean provided are `true` */
    fun assertAllTrue(vararg boolean: Boolean) =
        withClue(
            "Element at index ${boolean.indexOf(false)} expected to be `true`",
        ) { boolean.all { it }.shouldBeTrue() }

    /** Asserts all boolean provided are `false` */
    fun assertAllFalse(vararg boolean: Boolean) =
        withClue(
            "Element at index ${boolean.indexOf(true)} expected to be `false`",
        ) { boolean.none { it }.shouldBeTrue() }

    /** Asserts mutual structural equality for two [Term]s */
    fun assertStructurallyEquals(
        expected: Term,
        actual: Term,
    ) {
        withClue("$actual should be structurally equal to $expected, while it is not") {
            (expected structurallyEquals actual).shouldBeTrue()
            (actual structurallyEquals expected).shouldBeTrue()
        }
    }

    /** Asserts mutual equality for two [Term]s, using simple names for [Var]iables */
    fun assertEqualsUsingVariablesSimpleNames(
        expected: Term,
        actual: Term,
    ) = assertAllTrue(
        expected.equals(actual, false),
        actual.equals(expected, false),
    )

    /** Asserts mutual inequality for two [Term]s, using simple names for [Var]iables */
    fun assertNotEqualsUsingVariablesSimpleNames(
        expected: Term,
        actual: Term,
    ) = assertAllFalse(
        expected.equals(actual, false),
        actual.equals(expected, false),
    )

    /** Asserts mutual not structural equality for two [Term]s */
    fun assertNotStructurallyEquals(
        expected: Term,
        actual: Term,
    ) = assertAllFalse(
        expected structurallyEquals actual,
        actual structurallyEquals expected,
    )

    /** Asserts all types of equalities (normal, strict and structural) for two [Term]s */
    fun assertEqualities(
        expected: Term,
        actual: Term,
    ) {
        assertStructurallyEquals(expected, actual)
        actual shouldBe expected
        assertEqualsUsingVariablesSimpleNames(expected, actual)
    }

    /** Asserts not equality of all types (normal, strict and structural) for two [Term]s */
    fun assertNoEqualities(
        expected: Term,
        actual: Term,
    ) {
        actual shouldNotBe expected
        assertNotStructurallyEquals(expected, actual)
        assertNotEqualsUsingVariablesSimpleNames(expected, actual)
    }

    /** Asserts the [assertion] for each [E] versus all the [E]s (itself included). */
    fun <E : Any> assertAllVsAll(
        toBeTested: Iterable<E>,
        assertion: (E, E) -> Unit,
    ) {
        // materialized once, since callers may pass single-use iterables (e.g. from generateSequence)
        val items = toBeTested.toList()
        for (first in items) {
            for (second in items) {
                assertion(first, second)
            }
        }
    }
}
