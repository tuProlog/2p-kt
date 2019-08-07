package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertFalse
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [ConsImpl] and [Cons]
 *
 * @author Enrico
 */
internal class ConsImplTest {

    private val consInstances = ConsUtils.mixedConsInstances(::ConsImpl)
    private val consInstancesHeads = ConsUtils.mixedConsInstancesHeads
    private val consInstancesTails = ConsUtils.mixedConsInstancesTails(::ConsImpl)
    private val consInstancesElementLists = ConsUtils.mixedConsInstancesElementLists
    private val consInstancesUnfoldedLists = ConsUtils.mixedConsInstancesUnfoldedLists

    @Test
    fun consFunctor() {
        consInstances.forEach { assertEquals(".", it.functor) }
    }

    @Test
    fun headCorrect() {
        onCorrespondingItems(consInstancesHeads, consInstances.map { it.head }, ::assertEqualities)
    }

    @Test
    fun tailCorrect() {
        onCorrespondingItems(consInstancesTails, consInstances.map { it.tail }, ::assertEqualities)
    }

    @Test
    fun argsCorrect() {
        val consInstancesArgs = consInstancesHeads.zip(consInstancesTails).map { (head, tail) -> arrayOf(head, tail) }

        onCorrespondingItems(consInstancesArgs, consInstances.map { it.args }) { expectedArgs, actualArgs ->
            assertEquals(expectedArgs.toList(), actualArgs.toList())
            assertTrue { expectedArgs.contentDeepEquals(actualArgs) }
        }
    }

    @Test
    fun unfoldedListCorrect() {
        onCorrespondingItems(consInstancesUnfoldedLists, consInstances.map { it.unfoldedList }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun unfoldedSequenceCorrect() {
        onCorrespondingItems(consInstancesUnfoldedLists, consInstances.map { it.unfoldedSequence.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun unfoldedArrayCorrect() {
        onCorrespondingItems(consInstancesUnfoldedLists, consInstances.map { it.unfoldedArray.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toListReturnValue() {
        onCorrespondingItems(consInstancesElementLists, consInstances.map { it.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toArrayReturnValue() {
        onCorrespondingItems(consInstancesElementLists, consInstances.map { it.toArray().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toSequenceReturnValue() {
        onCorrespondingItems(consInstancesElementLists, consInstances.map { it.toSequence().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToString = ConsUtils.mixedConsInstancesCorrectToString

        onCorrespondingItems(correctToString, consInstances.map { it.toString() }) { expectedString, actualString ->
            assertEquals(expectedString, actualString)
        }
    }

    @Test
    fun sizeCorrect() {
        val correctSizes = consInstancesElementLists.map { it.size }

        onCorrespondingItems(correctSizes, consInstances.map { it.size }) { expectedSize, actualSize ->
            assertEquals(expectedSize, actualSize)
        }
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        assertFalse(
                ConsUtils.oneElementList(::ConsImpl).isGround,
                ConsUtils.twoElementList(::ConsImpl).isGround,
                ConsUtils.twoElementListWithPipe(::ConsImpl).isGround,
                ConsUtils.threeElementListWithPipe(::ConsImpl).isGround
        )

        assertTrue(ConsUtils.threeElementList(::ConsImpl).isGround)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        consInstances.forEach(TermTypeAssertionUtils::assertIsCons)
    }

    @Test
    fun twoArity() {
        consInstances.forEach { assertEquals(it.arity, 2) }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        consInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        consInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::ConsImpl)
    }
}
