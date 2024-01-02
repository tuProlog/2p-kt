package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.BlockUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [BlockImpl] and [Block]
 *
 * @author Enrico
 */
internal class BlockImplTest {
    private val mixedBlocksInstances = BlockUtils.mixedBlocksTupleWrapped.map(::BlockImpl)

    @Test
    fun blockFunctor() {
        mixedBlocksInstances.forEach { assertEquals("{}", it.functor) }
    }

    @Test
    fun argsCorrect() {
        onCorrespondingItems(
            BlockUtils.mixedBlocksTupleWrapped,
            mixedBlocksInstances.map { it.args.first() },
            ::assertEqualities,
        )
    }

    @Test
    fun unfoldedListCorrect() {
        val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(correctElementLists, mixedBlocksInstances.map { it.unfoldedList }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun unfoldedSequenceCorrect() {
        val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(
            correctElementLists,
            mixedBlocksInstances.map { it.unfoldedSequence.toList() },
        ) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun unfoldedArrayCorrect() {
        val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(
            correctElementLists,
            mixedBlocksInstances.map { it.unfoldedArray.toList() },
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun toStringShouldWorkAsExpected() {
        val blocksToString = BlockUtils.mixedBlocks.map { "{${it.joinToString(", ")}}" }

        onCorrespondingItems(
            blocksToString,
            mixedBlocksInstances.map { it.toString() },
        ) { expectedToString, actualToString -> assertEquals(expectedToString, actualToString) }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedBlocksInstances.forEach(TermTypeAssertionUtils::assertIsBlock)
    }

    @Test
    fun isGroundTrueOnlyIfNoVariablesArePresent() {
        val groundBlocksInstances = BlockUtils.groundBlocksTupleWrapped.map(::BlockImpl)
        val nonGroundBlocksInstances = BlockUtils.nonGroundBlocksTupleWrapped.map(::BlockImpl)

        groundBlocksInstances.forEach { assertTrue { it.isGround } }
        nonGroundBlocksInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun emptyBlockDetected() {
        assertTrue(BlockImpl(null).isEmptyBlock)
    }

    @Test
    fun toListReturnValue() {
        val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(mixedBlocks, mixedBlocksInstances.map { it.toList() }) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toArrayReturnValue() {
        val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(
            mixedBlocks,
            mixedBlocksInstances.map { it.toArray().toList() },
        ) { expectedList, actualList ->
            assertEquals(expectedList, actualList)
        }
    }

    @Test
    fun toSequenceReturnValue() {
        val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

        onCorrespondingItems(
            mixedBlocks,
            mixedBlocksInstances.map { it.toSequence().toList() },
        ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        mixedBlocksInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        mixedBlocksInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        val blockWithSameVarName = BlockImpl(Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")))

        assertAllVsAll(blockWithSameVarName.args) { anElement, anotherElement ->
            assertEqualities(anElement, anotherElement)
            assertSame(anElement, anotherElement)
        }

        val blockCopied = blockWithSameVarName.freshCopy()
        assertAllVsAll(blockCopied.args, ::assertEqualities)
    }
}
