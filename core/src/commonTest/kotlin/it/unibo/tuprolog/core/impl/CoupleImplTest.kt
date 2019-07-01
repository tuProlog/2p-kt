package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [CoupleImpl] and [Couple]
 *
 * @author Enrico
 */
internal class CoupleImplTest {

    private val headOfFirstList = Var.of("H")
    private val tailOfFirstList = Empty.list()
    private val oneElementList = CoupleImpl(headOfFirstList, tailOfFirstList)
    private val unfoldedFirstList = listOf(headOfFirstList)

    private val headOfSecondList = Var.of("H")
    private val tailOfSecondList = Var.of("T")
    private val twoElementList = CoupleImpl(headOfSecondList, tailOfSecondList)
    private val unfoldedSecondList = listOf(headOfSecondList, tailOfSecondList)

    private val headOfThirdList = Atom.of("bigList")
    private val tailOfThirdListFirstElement = Integral.of(4)
    private val tailOfThirdListSecondElement = Real.of(1.5)
    private val tailOfThirdList = CoupleImpl(tailOfThirdListFirstElement, tailOfThirdListSecondElement)
    private val threeElementList = CoupleImpl(headOfThirdList, tailOfThirdList)
    private val unfoldedThirdList = listOf(headOfThirdList, tailOfThirdListFirstElement, tailOfThirdListSecondElement)

    private val coupleInstances = listOf(oneElementList, twoElementList, threeElementList)
    private val coupleInstancesHeads = listOf(headOfFirstList, headOfSecondList, headOfThirdList)
    private val coupleInstancesTails = listOf(tailOfFirstList, tailOfSecondList, tailOfThirdList)
    private val coupleInstancesUnfoldedLists = listOf(unfoldedFirstList, unfoldedSecondList, unfoldedThirdList)

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
    fun toListReturnValue() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toArrayReturnValue() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.toArray().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toSequenceReturnValue() {
        onCorrespondingItems(coupleInstancesUnfoldedLists, coupleInstances.map { it.toSequence().toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val coupleInstanceStringRepr = listOf("[H]", "[H, T]", "[bigList, 4, 1.5]")

        onCorrespondingItems(coupleInstanceStringRepr, coupleInstances.map { it.toString() }) { expectedString, actualString ->
            assertEquals(expectedString, actualString)
        }
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        assertFalse(oneElementList.isGround)
        assertFalse(twoElementList.isGround)
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
        val groundCoupleInstances = coupleInstances.filterNot { it.isGround }
        val copiedCoupleInstances = groundCoupleInstances.map { it.freshCopy() }

        onCorrespondingItems(groundCoupleInstances, copiedCoupleInstances) { original, copy ->
            assertEquals(original, copy)
            assertStructurallyEquals(original, copy)
            assertNotStrictlyEquals(original, copy)
            assertNotSame(original, copy)
        }
    }

    @Test
    fun freshCopyShouldRenewVariablesAccountingOfTheirNames() {
        val coupleVar = Var.of("A")
        val coupleWithSameVarName = CoupleImpl(coupleVar, coupleVar)

        assertEqualities(coupleWithSameVarName.head, coupleWithSameVarName.tail)
        assertSame(coupleWithSameVarName.head, coupleWithSameVarName.tail)

        val coupleCopied = coupleWithSameVarName.freshCopy() as Couple

        // TODO this will not work now, to implement correct freshCopy (Issue #14)
        // assertEqualities(coupleCopied.head, coupleCopied.tail)
    }
}
