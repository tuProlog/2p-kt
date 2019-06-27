package it.unibo.tuprolog.core.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for [EmptyListImpl]
 *
 * @author Enrico
 */
internal class EmptyListImplTest {

    private val testedObj = EmptyListImpl

    @Test
    fun emptyListFunctor() {
        assertEquals(testedObj.functor, "[]")
    }

    @Test
    fun toArrayReturnValue() {
        assertTrue(testedObj.toArray().isEmpty())
    }

    @Test
    fun toSequenceReturnValue() {
        testedObj.toSequence().onEach { fail("Sequence should be empty!") }
    }

    @Test
    fun toListReturnValue() {
        assertTrue(testedObj.toList().isEmpty())
    }
}
