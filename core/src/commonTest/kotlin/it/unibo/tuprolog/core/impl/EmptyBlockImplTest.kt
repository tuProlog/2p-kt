package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [EmptyBlockImpl] and [EmptyBlock]
 *
 * @author Enrico
 */
internal class EmptyBlockImplTest {
    private val testedObj = EmptyBlockImpl()

    @Test
    fun emptyBlockFunctor() {
        assertEquals("{}", testedObj.functor)
    }

    @Test
    fun emptyArguments() {
        AtomUtils.assertNoArguments(testedObj)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        TermTypeAssertionUtils.assertIsEmptyBlock(testedObj)
    }

    @Test
    fun variablesEmpty() {
        assertTrue { testedObj.variables.none() }
    }

    @Test
    fun functorDoesNotRespectStructRegex() {
        assertFalse(testedObj.isFunctorWellFormed)
    }

    @Test
    fun emptyBlockFreshCopyShouldReturnTheInstanceItself() {
        ConstantUtils.assertFreshCopyIsItself(testedObj)
    }

    @Test
    fun emptyBlockFreshCopyWithScopeShouldReturnTheInstanceItself() {
        ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
    }
}
