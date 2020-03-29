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

    private val `true` = TruthImpl.TRUE
    private val fail = TruthImpl.FAIL

    private val truthInstances = listOf(`true`, fail)

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
    fun truthFreshCopyShouldReturnTheInstanceItself() {
        truthInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun truthFreshCopyWithScopeShouldReturnTheInstanceItself() {
        truthInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }
}
