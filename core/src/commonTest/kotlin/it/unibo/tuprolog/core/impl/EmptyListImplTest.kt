package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [EmptyListImpl] and [EmptyList]
 *
 * @author Enrico
 */
internal class EmptyListImplTest {
    private val testedObj = EmptyListImpl()

    @Test
    fun emptyListFunctor() {
        assertEquals("[]", testedObj.functor)
    }

    @Test
    fun unfoldedListContainsEmptyList() {
        assertEquals(listOf(EmptyListImpl()), testedObj.unfoldedList)
    }

    @Test
    fun unfoldedSequenceContainsEmptyList() {
        assertEquals(sequenceOf(EmptyListImpl()).toList(), testedObj.unfoldedSequence.toList())
    }

    @Test
    fun unfoldedArrayContainsEmptyList() {
        assertTrue(arrayOf(EmptyListImpl()).contentDeepEquals(testedObj.unfoldedArray))
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
    fun toStringShouldWorkAsExpected() {
        assertEquals("[]", testedObj.toString())
    }

    @Test
    fun sizeShouldBeZero() {
        assertEquals(0, testedObj.size)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        TermTypeAssertionUtils.assertIsEmptyList(testedObj)
    }

    @Test
    fun functorDoesNotRespectStructRegex() {
        assertFalse(testedObj.isFunctorWellFormed)
    }

    @Test
    fun emptyListFreshCopyShouldReturnTheInstanceItself() {
        ConstantUtils.assertFreshCopyIsItself(testedObj)
    }

    @Test
    fun emptyListFreshCopyWithScopeShouldReturnTheInstanceItself() {
        ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
    }
}
