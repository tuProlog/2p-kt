package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [TruthImpl] and [Truth]
 *
 * @author Enrico
 */
internal class TruthImplTest {
    private val truthInstances = listOf(Truth.TRUE, Truth.FAIL, Truth.FALSE)

    @Test
    fun truthFunctor() {
        assertEquals("true", Truth.TRUE.functor)
        assertEquals("fail", Truth.FAIL.functor)
    }

    @Test
    fun testIsPropertiesAndTypesForTrue() {
        TermTypeAssertionUtils.assertIsTruth(Truth.TRUE)
        assertTrue(Truth.TRUE.isTrue)
    }

    @Test
    fun testIsPropertiesAndTypesForFail() {
        TermTypeAssertionUtils.assertIsTruth(Truth.FAIL)
        assertTrue(Truth.FAIL.isFail)
    }

    @Test
    fun truthFreshCopyShouldReturnTheInstanceItself() {
        truthInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun truthFreshCopyWithScopeShouldReturnTheInstanceItself() {
        truthInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }
}
