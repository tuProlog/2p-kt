package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.SetUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [SetImpl] and [Set]
 *
 * @author Enrico
 */
internal class SetImplTest {

    private val mixedSetsInstances = SetUtils.mixedSets.map(::SetImpl)

    @Test
    fun setFunctor() {
        mixedSetsInstances.forEach { assertEquals(it.functor, Set.FUNCTOR) }
    }

    @Test
    fun argsCorrect() {
        onCorrespondingItems(SetUtils.mixedSets, mixedSetsInstances.map { it.args }) { expectedArgs, actualArgs ->
            assertEquals(expectedArgs.toList(), actualArgs.toList())
            assertTrue { expectedArgs.contentDeepEquals(actualArgs) }
        }
    }

    @Test
    fun toStringShouldWorkAsExpected() {
        val setsToString = SetUtils.mixedSets.map { "{${it.joinToString(", ")}}" }

        onCorrespondingItems(setsToString, mixedSetsInstances.map { it.toString() }) { expectedToString, actualToString ->
            assertEquals(expectedToString, actualToString)
        }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedSetsInstances.forEach(TermTypeAssertionUtils::assertIsSet)
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        val groundSetsInstances = SetUtils.groundSets.map { SetImpl(it) }
        val nonGroundSetsInstances = SetUtils.notGroundSets.map { SetImpl(it) }

        groundSetsInstances.forEach { assertTrue { it.isGround } }
        nonGroundSetsInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun emptySetDetected() {
        assertTrue(SetImpl(arrayOf()).isEmptySet)
    }

    @Test
    fun toListReturnValue() {
        val mixedSets = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(mixedSets, mixedSetsInstances.map { it.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toArrayReturnValue() {
        val mixedSets = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(mixedSets, mixedSetsInstances.map { it.toArray().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toSequenceReturnValue() {
        val mixedSets = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(mixedSets, mixedSetsInstances.map { it.toSequence().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        mixedSetsInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        val groundSetInstances = mixedSetsInstances.filterNot { it.isGround }
        val copiedSetInstances = groundSetInstances.map { it.freshCopy() }

        onCorrespondingItems(groundSetInstances, copiedSetInstances) { original, copy ->
            assertEquals(original, copy)
            AssertionUtils.assertStructurallyEquals(original, copy)
            AssertionUtils.assertNotStrictlyEquals(original, copy)
            assertNotSame(original, copy)
        }
    }

    @Test
    fun freshCopyShouldRenewVariablesAccountingOfTheirNames() {
        val setVar = Var.of("A")
        val setWithSameVarName = SetImpl(arrayOf(setVar, setVar, setVar))

        assertAllVsAll(setWithSameVarName.argsList) { anElement, anotherElement ->
            assertEqualities(anElement, anotherElement)
            assertSame(anElement, anotherElement)
        }

        val setCopied = setWithSameVarName.freshCopy() as Set

        // TODO this will not work now, to implement correct freshCopy (Issue #14)
        // assertAllVsAll(setCopied.argsList, ::assertEqualities)
    }

}
