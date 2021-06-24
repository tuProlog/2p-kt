package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.SetUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [SetImpl] and [Set]
 *
 * @author Enrico
 */
internal class SetImplTest {

    private val mixedSetsInstances = SetUtils.mixedSetsTupleWrapped.map(::SetImpl)

    @Test
    fun setFunctor() {
        mixedSetsInstances.forEach { assertEquals("{}", it.functor) }
    }

    @Test
    fun argsCorrect() {
        onCorrespondingItems(
            SetUtils.mixedSetsTupleWrapped,
            mixedSetsInstances.map { it.args.first() },
            ::assertEqualities
        )
    }

    @Test
    fun unfoldedListCorrect() {
        val correctElementLists = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(correctElementLists, mixedSetsInstances.map { it.unfoldedList }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun unfoldedSequenceCorrect() {
        val correctElementLists = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(
            correctElementLists,
            mixedSetsInstances.map { it.unfoldedSequence.toList() }
        ) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun unfoldedArrayCorrect() {
        val correctElementLists = SetUtils.mixedSets.map { it.toList() }

        onCorrespondingItems(
            correctElementLists,
            mixedSetsInstances.map { it.unfoldedArray.toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toStringShouldWorkAsExpected() {
        val setsToString = SetUtils.mixedSets.map { "{${it.joinToString(", ")}}" }

        onCorrespondingItems(
            setsToString,
            mixedSetsInstances.map { it.toString() }
        ) { expectedToString, actualToString -> assertEquals(expectedToString, actualToString) }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedSetsInstances.forEach(TermTypeAssertionUtils::assertIsSet)
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        val groundSetsInstances = SetUtils.groundSetsTupleWrapped.map(::SetImpl)
        val nonGroundSetsInstances = SetUtils.notGroundSetsTupleWrapped.map(::SetImpl)

        groundSetsInstances.forEach { assertTrue { it.isGround } }
        nonGroundSetsInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun emptySetDetected() {
        assertTrue(SetImpl(null).isEmptySet)
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

        onCorrespondingItems(
            mixedSets,
            mixedSetsInstances.map { it.toSequence().toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        mixedSetsInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        mixedSetsInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        val setWithSameVarName = SetImpl(Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")))

        assertAllVsAll(setWithSameVarName.args) { anElement, anotherElement ->
            assertEqualities(anElement, anotherElement)
            assertSame(anElement, anotherElement)
        }

        val setCopied = setWithSameVarName.freshCopy()
        assertAllVsAll(setCopied.args, ::assertEqualities)
    }
}
