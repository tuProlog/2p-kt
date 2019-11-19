package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [TupleImpl] and [Tuple]
 *
 * @author Enrico
 */
internal class TupleImplTest {

    private val twoElementsTuple = TupleUtils.twoElementTuple(::TupleImpl)
    private val threeElementsTuple = TupleUtils.threeElementTuple(::TupleImpl)

    private val tupleInstances = TupleUtils.tupleInstances(::TupleImpl)
    private val tupleInstancesLefts = TupleUtils.tupleInstancesLefts
    private val tupleInstancesRights = TupleUtils.tupleInstancesRights(::TupleImpl)
    private val tupleInstancesElementLists = TupleUtils.tupleInstancesElementLists

    @Test
    fun tupleFunctor() {
        tupleInstances.forEach { assertEquals(",", it.functor) }
    }

    @Test
    fun leftCorrect() {
        onCorrespondingItems(tupleInstancesLefts, tupleInstances.map { it.left }, ::assertEqualities)
    }

    @Test
    fun rightCorrect() {
        onCorrespondingItems(tupleInstancesRights, tupleInstances.map { it.right }, ::assertEqualities)
    }

    @Test
    fun argsCorrect() {
        val tupleInstancesArgs =
            tupleInstancesLefts.zip(tupleInstancesRights).map { (left, right) -> arrayOf(left, right) }

        onCorrespondingItems(tupleInstancesArgs, tupleInstances.map { it.args }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
            assertTrue { expected.contentDeepEquals(actual) }
        }
    }

    @Test
    fun unfoldedListCorrect() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.unfoldedList }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun unfoldedSequenceCorrect() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.unfoldedSequence.toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun unfoldedArrayCorrect() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.unfoldedArray.toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toListReturnValue() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toArrayReturnValue() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.toArray().toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toSequenceReturnValue() {
        onCorrespondingItems(
            tupleInstancesElementLists,
            tupleInstances.map { it.toSequence().toList() }
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toStringWorksAsExpected() {
        val tupleToStrings = tupleInstancesElementLists.map { it.joinToString(", ", "(", ")") }

        onCorrespondingItems(tupleToStrings, tupleInstances.map { it.toString() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        tupleInstances.forEach(TermTypeAssertionUtils::assertIsTuple)
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        assertFalse(twoElementsTuple.isGround)
        assertTrue(threeElementsTuple.isGround)
    }

    @Test
    fun twoArity() {
        tupleInstances.forEach { assertEquals(2, it.arity) }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        tupleInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        tupleInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::TupleImpl)
    }
}
