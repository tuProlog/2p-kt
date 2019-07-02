package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.SetUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.*

/**
 * Test class for [SetImpl] and [Set]
 *
 * @author Enrico
 */
internal class SetImplTest {

    private val mixedSetsInstances = SetUtils.mixedSets.map(this::newSet)

    private fun newSet(terms: Array<Term>): SetImpl {
        return if (terms.isEmpty()) {
            EmptySetImpl
        } else {
            SetImpl(Tuple.wrapIfNeeded(*terms))
        }
    }

    @Test
    fun setFunctor() {
        mixedSetsInstances.forEach { assertEquals(it.functor, ",") }
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
        val groundSetsInstances = SetUtils.groundSets.map(this::newSet)
        val nonGroundSetsInstances = SetUtils.notGroundSets.map(this::newSet)

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
        mixedSetsInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyShouldRenewVariablesTakingAccountOfTheirNames() {
        val setVar = Var.of("A")
        val setWithSameVarName = newSet(arrayOf(setVar, setVar, setVar))

        assertAllVsAll(setWithSameVarName.argsList) { anElement, anotherElement ->
            assertEqualities(anElement, anotherElement)
            assertSame(anElement, anotherElement)
        }

//        val setCopied = setWithSameVarName.freshCopy() as Set

        // TODO this will not work now, to implement correct freshCopy (Issue #14)
        // assertAllVsAll(setCopied.argsList, ::assertEqualities)
    }

}
