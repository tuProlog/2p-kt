package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptySetImpl
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * Test class for [EmptySet] companion object
 *
 * @author Enrico
 */
internal class EmptySetTest {

    @Test
    fun emptySetCompanionReturnsEmptySetImpl() {
        assertSame(EmptySetImpl(), EmptySet())
    }
}
