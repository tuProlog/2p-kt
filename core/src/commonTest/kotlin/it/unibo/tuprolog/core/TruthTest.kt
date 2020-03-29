package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * Test class for [Truth] companion object
 *
 * @author Enrico
 */
internal class TruthTest {

    @Test
    fun trueTruthCreation() {
        assertSame(TruthImpl.True, Truth.of(true))
    }

    @Test
    fun trueTruthRetrieval() {
        assertSame(TruthImpl.True, Truth.TRUE)
    }

    @Test
    fun falseTruthCreation() {
        assertSame(TruthImpl.False, Truth.of(false))
    }

    @Test
    fun failTruthRetrieval() {
        assertSame(TruthImpl.Fail, Truth.FAIL)
    }

    @Test
    fun falseTruthRetrieval() {
        assertSame(TruthImpl.False, Truth.FALSE)
    }
}
