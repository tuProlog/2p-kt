package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [EmptySetImpl] and [EmptySet]
 *
 * @author Enrico
 */
internal class EmptySetImplTest {

    private val testedObj = EmptySetImpl

    @Test
    fun emptySetFunctor() {
        assertEquals("{}", testedObj.functor)
    }

    @Test
    fun emptyArguments() {
        AtomUtils.assertNoArguments(testedObj)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        TermTypeAssertionUtils.assertIsEmptySet(testedObj)
    }

    @Test
    fun emptyListCompanionReturnsEmptyListImpl() {
        assertSame(EmptySet(), testedObj)
    }
}