package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
internal class AtomImplTest {

    private val mixedAtomInstances = AtomUtils.mixedAtoms.map(::AtomImpl)

    @Test
    fun functorCorrectness() {
        onCorrespondingItems(AtomUtils.mixedAtoms, mixedAtomInstances.map { it.functor }) { atomString, atomInstanceFunctor ->
            assertEquals(atomString, atomInstanceFunctor)
        }
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
    fun testIsPropertiesAndTypesForNonSpecialAtom() {
        AtomUtils.nonSpecialAtoms.map(::AtomImpl)
                .forEach(TermTypeAssertionUtils::assertIsAtom)
    }

    @Test
    fun emptySetAtomDetected() {
        assertTrue(AtomImpl("{}").isEmptySet)
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
    fun strictlyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        // TODO review this behaviour, this is maybe incorrect
        assertTrue(trueStruct strictlyEquals trueAtom)
        assertFalse(trueAtom strictlyEquals trueStruct)

        assertTrue(trueAtom strictlyEquals trueTruth)
        assertTrue(trueTruth strictlyEquals trueAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        assertTrue(trueStruct structurallyEquals trueAtom)
        assertTrue(trueAtom structurallyEquals trueStruct)

        assertTrue(trueAtom structurallyEquals trueTruth)
        assertTrue(trueTruth structurallyEquals trueAtom)
    }

    @Test
    fun atomFreshCopyShouldReturnTheInstanceItself() {
        mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun atomFreshCopyWithScopeShouldReturnTheInstanceItself() {
        mixedAtomInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }
}
