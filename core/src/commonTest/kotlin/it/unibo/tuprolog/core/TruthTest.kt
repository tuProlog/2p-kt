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
        assertSame(TruthImpl.TRUE, Truth.of(true))
    }

    @Test
    fun trueTruthRetrieval() {
        assertSame(TruthImpl.TRUE, Truth.TRUE)
    }

    @Test
    fun falseTruthCreation() {
        assertSame(TruthImpl.FALSE, Truth.of(false))
    }

    @Test
    fun failTruthRetrieval() {
        assertSame(TruthImpl.FAIL, Truth.FAIL)
    }

    @Test
    fun falseTruthRetrieval() {
        assertSame(TruthImpl.FALSE, Truth.FALSE)
    }
}
