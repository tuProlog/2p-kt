package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Utils singleton to test equality
 *
 * @author Enrico
 */
internal object EqualityUtils {

    /**
     * Asserts mutual structural equality for two [Term]s
     */
    fun assertStructurallyEquals(toTest: Term, correct: Term) {
        assertTrue { toTest structurallyEquals correct }
        assertTrue { correct structurallyEquals toTest }
    }

    /**
     * Asserts all types of equalities (normal, strict and structural) for two [Term]s
     */
    fun assertEqualities(toTest: Term, correct: Term) {
        assertEquals(correct, toTest)
        assertTrue { toTest strictlyEquals correct }
        assertTrue { correct strictlyEquals toTest }
        assertStructurallyEquals(toTest, correct)
    }

    /**
     * Asserts all types of equalities (normal, strict and structural) for two [Term]s lists.
     *
     * The comparison is done for corresponding items in order.
     */
    @JsName("assertElementsEqualities")
    fun assertEqualities(toBeTested: Iterable<Term>, correct: Iterable<Term>) {
        toBeTested.zip(correct).forEach { (toTest, correct) ->
            assertEqualities(toTest, correct)
        }
    }

    /**
     * Asserts structural equality for two [Term]s lists.
     *
     * The comparison is done for corresponding items in order.
     */
    fun assertStructurallyEquals(toBeTested: Iterable<Term>, correct: Iterable<Term>) {
        toBeTested.zip(correct).forEach { (toTest, correct) ->
            assertStructurallyEquals(toTest, correct)
        }
    }

    /**
     * Asserts all types of qualities (normal, strict, and structural) for each [Term] versus all the [Term]s (itself included).
     */
    fun assertAllVsAllEqualities(toBeTested: Iterable<Term>) {
        val toTestItems = toBeTested.count()
        val repeatedElementsSequence = toBeTested.flatMap { testTerm ->
            generateSequence { testTerm }.take(toTestItems).asIterable()
        }
        val repeatedSequenceOfElements = generateSequence { toBeTested }
                .take(toTestItems).flatten().asIterable()

        assertEqualities(
                repeatedElementsSequence,
                repeatedSequenceOfElements)
    }

    /**
     * Asserts mutual not structural equality for two [Term]s
     */
    fun assertNotStructurallyEquals(toTest: Term, correct: Term) {
        assertFalse { toTest structurallyEquals correct }
        assertFalse { correct structurallyEquals toTest }
    }

    /**
     * Asserts not equality of all types (normal, strict and structural) for two [Term]s
     */
    fun assertNoEqualities(toTest: Term, correct: Term) {
        assertNotEquals(correct, toTest)
        assertFalse { toTest strictlyEquals correct }
        assertFalse { correct strictlyEquals toTest }
        assertNotStructurallyEquals(toTest, correct)
    }
}