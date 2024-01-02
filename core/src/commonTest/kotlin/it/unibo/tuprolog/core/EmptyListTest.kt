package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyListImpl
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [EmptyList] companion object
 *
 * @author Enrico
 */
internal class EmptyListTest {
    @Test
    fun emptyListCompanionReturnsEmptyListImpl() {
        assertEquals(EmptyListImpl(), EmptyList())
    }
}
