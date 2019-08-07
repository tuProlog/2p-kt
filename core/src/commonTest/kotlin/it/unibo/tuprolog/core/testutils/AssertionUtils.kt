package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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
    fun assertTrue(vararg boolean: Boolean) =
            assertTrue { boolean.all { it } }

    /** Asserts all boolean provided are `false` */
    fun assertFalse(vararg boolean: Boolean) =
            assertFalse { boolean.none { it } }

    /** Asserts mutual structural equality for two [Term]s */
    fun assertStructurallyEquals(expected: Term, actual: Term) =
            assertTrue(
                    expected structurallyEquals actual,
                    actual structurallyEquals expected
            )


    /** Asserts mutual not structural equality for two [Term]s */
    fun assertNotStructurallyEquals(expected: Term, actual: Term) =
            assertFalse(
                    expected structurallyEquals actual,
                    actual structurallyEquals expected
            )


    /** Asserts mutual strict equality for two [Term]s */
    fun assertStrictlyEquals(expected: Term, actual: Term) =
            assertTrue(
                    expected strictlyEquals actual,
                    actual strictlyEquals expected
            )


    /** Asserts mutual not strict equality for two [Term]s */
    fun assertNotStrictlyEquals(expected: Term, actual: Term) =
            assertFalse(
                    expected strictlyEquals actual,
                    actual strictlyEquals expected
            )


    /** Asserts all types of equalities (normal, strict and structural) for two [Term]s */
    fun assertEqualities(expected: Term, actual: Term) {
        assertStructurallyEquals(expected, actual)
        assertEquals(expected, actual)
        assertStrictlyEquals(expected, actual)
    }

    /** Asserts not equality of all types (normal, strict and structural) for two [Term]s */
    fun assertNoEqualities(expected: Term, actual: Term) {
        assertNotEquals(expected, actual)
        assertNotStructurallyEquals(expected, actual)
        assertNotStrictlyEquals(expected, actual)
    }

    /** Executes the [function] with corresponding items in order */
    fun <A, B> onCorrespondingItems(expected: Iterable<A>, actual: Iterable<B>, function: (A, B) -> Unit) =
            expected.zip(actual).forEach { (expected, actual) -> function(expected, actual) }

    /** Asserts the [assertion] for each [E] versus all the [E]s (itself included). */
    fun <E : Any> assertAllVsAll(toBeTested: Iterable<E>, assertion: (E, E) -> Unit) {
        val toTestItems = toBeTested.count()
        val repeatedElementsSequence = toBeTested.flatMap { underTestItem ->
            generateSequence { underTestItem }.take(toTestItems).asIterable()
        }
        val repeatedSequenceOfElements = generateSequence { toBeTested }
                .take(toTestItems).flatten().asIterable()

        onCorrespondingItems(repeatedElementsSequence, repeatedSequenceOfElements, assertion)
    }
}