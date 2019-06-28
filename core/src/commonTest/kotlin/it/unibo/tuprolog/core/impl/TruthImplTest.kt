package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [TruthImpl] and [Truth]
 *
 * @author Enrico
 */
internal class TruthImplTest {

    private val `true` = TruthImpl.True
    private val fail = TruthImpl.Fail

    @Test
    fun truthFunctor() {
        assertEquals("true", `true`.functor)
        assertEquals("fail", fail.functor)
    }

    @Test
    fun testIsPropertiesAndTypesForTrue() {
        TermTypeAssertionUtils.assertIsTruth(`true`)
        assertTrue(`true`.isTrue)
    }

    @Test
    fun testIsPropertiesAndTypesForFail() {
        TermTypeAssertionUtils.assertIsTruth(fail)
        assertTrue(fail.isFail)
    }

    @Test
    fun trueTruthCreation() {
        assertSame(`true`, Truth.of(true))
    }

    @Test
    fun trueTruthRetrieval() {
        assertSame(`true`, Truth.`true`())
    }

    @Test
    fun failTruthCreation() {
        assertSame(fail, Truth.of(false))
    }

    @Test
    fun failTruthRetrieval() {
        assertSame(fail, Truth.fail())
    }
}
