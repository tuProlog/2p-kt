package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [Truth] companion object
 *
 * @author Enrico
 */
internal class TruthTest {

    private val TRUE = TruthImpl(Terms.TRUE_FUNCTOR, true)
    private val FAIL = TruthImpl(Terms.FAIL_FUNCTOR, false)
    private val FALSE = TruthImpl(Terms.FALSE_FUNCTOR, false)

    @Test
    fun trueTruthCreation() {
        assertSame(Truth.TRUE, Truth.of(true))
    }

    @Test
    fun trueTruthRetrieval() {
        assertEquals(TRUE, Truth.TRUE)
    }

    @Test
    fun falseTruthCreation() {
        assertSame(Truth.FALSE, Truth.of(false))
    }

    @Test
    fun falseTruthRetrieval() {
        assertEquals(FALSE, Truth.FALSE)
    }

    @Test
    fun failTruthRetrieval() {
        assertEquals(FAIL, Truth.FAIL)
    }
}
