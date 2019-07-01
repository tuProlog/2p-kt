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
        assertSame(TruthImpl.True, Truth.`true`())
    }

    @Test
    fun failTruthCreation() {
        assertSame(TruthImpl.Fail, Truth.of(false))
    }

    @Test
    fun failTruthRetrieval() {
        assertSame(TruthImpl.Fail, Truth.fail())
    }
}
