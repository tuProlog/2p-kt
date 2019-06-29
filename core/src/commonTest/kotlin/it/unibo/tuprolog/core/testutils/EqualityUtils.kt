package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Utils singleton to test equality
 *
 * @author Enrico
 */
internal object EqualityUtils {

    /**
     * Utility function to test all types of equalities (normal, strict and structural) of two Term lists
     */
    fun assertElementsEqualities(toBeTested: Iterable<Term>, correct: Iterable<Term>) {
        toBeTested.zip(correct).forEach { (toTestAtom, correctAtom) ->
            assertEquals(correctAtom, toTestAtom)
            assertTrue { toTestAtom strictlyEquals correctAtom }
            assertTrue { correctAtom strictlyEquals toTestAtom }
            assertTrue { toTestAtom structurallyEquals correctAtom }
            assertTrue { correctAtom structurallyEquals toTestAtom }
        }
    }

    /**
     * Utility function to test structural equality of all corresponding elements
     */
    fun assertElementsStructuralEquality(toBeTested: Iterable<Term>, correct: Iterable<Term>) {
        toBeTested.zip(correct).forEach { (toTestAtom, correctAtom) ->
            assertTrue { toTestAtom structurallyEquals correctAtom }
            assertTrue { correctAtom structurallyEquals toTestAtom }
        }
    }
}