package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [EmptyListImpl] and [EmptyList]
 *
 * @author Enrico
 */
internal class EmptyListImplTest {

    private val testedObj = EmptyListImpl

    @Test
    fun emptyListFunctor() {
        assertEquals("[]", testedObj.functor)
    }

    @Test
    fun functorDoesNotRespectStructRegex() {
        assertFalse(testedObj.isFunctorWellFormed)
    }

    @Test
    fun toArrayReturnValue() {
        assertTrue(testedObj.toArray().isEmpty())
    }

    @Test
    fun toSequenceReturnValue() {
        assertTrue(testedObj.toSequence().toList().isEmpty())
    }

    @Test
    fun toListReturnValue() {
        assertTrue(testedObj.toList().isEmpty())
    }

    @Test
    fun testIsPropertiesAndTypes() {
        TermTypeAssertionUtils.assertIsEmptyList(testedObj)
    }

    @Test
    fun emptyListCompanionReturnsEmptyListImpl() {
        assertSame(EmptyList(), testedObj)
    }
}
