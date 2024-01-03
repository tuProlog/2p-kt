package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [EmptyBlock] companion object
 *
 * @author Enrico
 */
internal class EmptyBlockTest {
    @Test
    fun emptyBlockCompanionReturnsEmptySetImpl() {
        assertEquals(EmptyBlockImpl(), EmptyBlock())
    }
}
