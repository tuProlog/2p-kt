package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
internal class AtomImplTest {
    private val mixedAtomInstances = AtomUtils.mixedAtoms.map(::AtomImpl)

    @Test
    fun functorCorrectness() {
        onCorrespondingItems(
            AtomUtils.mixedAtoms,
            mixedAtomInstances.map { it.functor },
        ) { atomString, atomInstanceFunctor -> assertEquals(atomString, atomInstanceFunctor) }
    }

    @Test
    fun atomFunctorAndValueAreTheSame() {
        mixedAtomInstances.forEach { assertSame(it.value, it.functor) }
    }

    @Test
    fun noArguments() {
        mixedAtomInstances.forEach(AtomUtils::assertNoArguments)
    }

    @Test
    fun zeroArity() {
        mixedAtomInstances.forEach { assertEquals(0, it.arity) }
    }

    @Test
    fun variablesEmpty() {
        mixedAtomInstances.forEach { assertTrue { it.variables.none() } }
    }

    @Test
    fun testIsPropertiesAndTypesForNonSpecialAtom() {
        AtomUtils.nonSpecialAtoms
            .map(::AtomImpl)
            .forEach(TermTypeAssertionUtils::assertIsAtom)
    }

    @Test
    fun emptyBlockAtomDetected() {
        assertTrue(AtomImpl("{}").isEmptyBlock)
    }

    @Test
    fun emptyListAtomDetected() {
        assertTrue(AtomImpl("[]").isEmptyList)
    }

    @Test
    fun trueAtomDetected() {
        assertTrue(AtomImpl("true").isTrue)
    }

    @Test
    fun failAtomDetected() {
        assertTrue(AtomImpl("fail").isFail)
    }

    @Test
    fun equalsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyList())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.TRUE

        assertEquals<Struct>(trueAtom, trueStruct)
        assertEquals<Atom>(trueAtom, trueTruth)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyList())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.TRUE

        assertStructurallyEquals(trueAtom, trueStruct)
        assertStructurallyEquals(trueAtom, trueTruth)
    }

    @Test
    fun atomFreshCopyShouldReturnTheInstanceItself() {
        mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun atomFreshCopyWithScopeShouldReturnTheInstanceItself() {
        mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }

    @Test
    fun applyAlwaysReturnsTheInstanceItself() {
        mixedAtomInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
    }
}
