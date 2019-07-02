package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Couple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.CoupleUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [CoupleImpl] and [Couple]
 *
 * @author Enrico
 */
internal class CoupleImplTest {

    private val oneElementList = CoupleUtils.oneElementList(::CoupleImpl)
    private val twoElementList = CoupleUtils.twoElementList(::CoupleImpl)
    private val threeElementList = CoupleUtils.threeElementList(::CoupleImpl)
    private val twoElementListWithPipe = CoupleUtils.twoElementListWithPipe(::CoupleImpl)

    private val coupleInstances = CoupleUtils.coupleInstances(::CoupleImpl)
    private val coupleInstancesHeads = CoupleUtils.coupleInstancesHeads
    private val coupleInstancesTails = CoupleUtils.coupleInstancesTails(::CoupleImpl)
    private val coupleInstancesElementLists = CoupleUtils.coupleInstancesElementLists
    private val coupleInstancesUnfoldedLists = CoupleUtils.coupleInstancesUnfoldedLists

    @Test
    fun coupleFunctor() {
        coupleInstances.forEach { assertEquals(it.functor, ".") }
    }

    @Test
    fun headCorrect() {
        onCorrespondingItems(coupleInstancesHeads, coupleInstances.map { it.head }, ::assertEqualities)
    }

    @Test
    fun tailCorrect() {
        onCorrespondingItems(coupleInstancesTails, coupleInstances.map { it.tail }, ::assertEqualities)
    }

    @Test
    fun argsCorrect() {
        val coupleInstancesArgs = coupleInstancesHeads.zip(coupleInstancesTails).map { (head, tail) -> arrayOf(head, tail) }

        onCorrespondingItems(coupleInstancesArgs, coupleInstances.map { it.args }) { expectedArgs, actualArgs ->
            assertEquals(expectedArgs.toList(), actualArgs.toList())
            assertTrue { expectedArgs.contentDeepEquals(actualArgs) }
        }
    }

    @Test
    fun unfoldedListCorrect() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.unfoldedList }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun unfoldedSequenceCorrect() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.unfoldedSequence.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun unfoldedArrayCorrect() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.unfoldedArray.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toListReturnValue() {
        onCorrespondingItems(coupleInstancesElementLists, coupleInstances.map { it.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toArrayReturnValue() {
        onCorrespondingItems(coupleInstancesElementLists, coupleInstances.map { it.toArray().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toSequenceReturnValue() {
        onCorrespondingItems(coupleInstancesElementLists, coupleInstances.map { it.toSequence().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val coupleInstanceStringRepr = listOf("[H]", "[H, T]", "[bigList, 4, 1.5]", "[Head | Tail]")

        onCorrespondingItems(coupleInstanceStringRepr, coupleInstances.map { it.toString() }) { expectedString, actualString ->
            assertEquals(expectedString, actualString)
        }
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        assertFalse(oneElementList.isGround)
        assertFalse(twoElementList.isGround)
        assertFalse(twoElementListWithPipe.isGround)
        assertTrue(threeElementList.isGround)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        coupleInstances.forEach(TermTypeAssertionUtils::assertIsCouple)
    }

    @Test
    fun twoArity() {
        coupleInstances.forEach { assertEquals(it.arity, 2) }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        coupleInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        coupleInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyShouldRenewVariablesTakingAccountOfTheirNames() {
        val coupleVar = Var.of("A")
        val coupleWithSameVarName = CoupleImpl(coupleVar, coupleVar)

        assertEqualities(coupleWithSameVarName.head, coupleWithSameVarName.tail)
        assertSame(coupleWithSameVarName.head, coupleWithSameVarName.tail)

        val coupleCopied = coupleWithSameVarName.freshCopy()

        assertEqualities(coupleCopied.head, coupleCopied.tail)
        assertSame(coupleCopied.head, coupleCopied.tail)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        val coupleWithSameVarName = CoupleImpl(Var.of("A"), Var.of("A"))

        assertStructurallyEquals(coupleWithSameVarName.head, coupleWithSameVarName.tail)
        assertNotStrictlyEquals(coupleWithSameVarName.head, coupleWithSameVarName.tail)
        assertNotSame(coupleWithSameVarName.head, coupleWithSameVarName.tail)

        val coupleCopied = coupleWithSameVarName.freshCopy()

        assertEqualities(coupleCopied.head, coupleCopied.tail)
        assertSame(coupleCopied.head, coupleCopied.tail)
    }
}
