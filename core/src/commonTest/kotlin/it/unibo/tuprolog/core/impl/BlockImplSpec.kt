package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [BlockImpl] and [Block]
 *
 * @author Enrico
 */
class BlockImplSpec :
    FunSpec({
        val mixedBlocksInstances = BlockUtils.mixedBlocksTupleWrapped.map(::BlockImpl)

        test("block-functor") {
            mixedBlocksInstances.forEach { assertEquals("{}", it.functor) }
        }

        test("args-correct") {
            onCorrespondingItems(
                BlockUtils.mixedBlocksTupleWrapped,
                mixedBlocksInstances.map { it.args.first() },
                ::assertEqualities,
            )
        }

        test("unfolded-list-correct") {
            val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(
                correctElementLists,
                mixedBlocksInstances.map { it.unfoldedList },
            ) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("unfolded-sequence-correct") {
            val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(
                correctElementLists,
                mixedBlocksInstances.map { it.unfoldedSequence.toList() },
            ) { expected, actual -> assertEquals(expected, actual) }
        }

        test("unfolded-array-correct") {
            val correctElementLists = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(
                correctElementLists,
                mixedBlocksInstances.map { it.unfoldedArray.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-string-should-work-as-expected") {
            val blocksToString = BlockUtils.mixedBlocks.map { "{${it.joinToString(", ")}}" }

            onCorrespondingItems(
                blocksToString,
                mixedBlocksInstances.map { it.toString() },
            ) { expectedToString, actualToString -> assertEquals(expectedToString, actualToString) }
        }

        test("is-properties-and-types") {
            mixedBlocksInstances.forEach(TermTypeAssertionUtils::assertIsBlock)
        }

        test("is-ground-true-only-if-no-variables-are-present") {
            val groundBlocksInstances = BlockUtils.groundBlocksTupleWrapped.map(::BlockImpl)
            val nonGroundBlocksInstances = BlockUtils.nonGroundBlocksTupleWrapped.map(::BlockImpl)

            groundBlocksInstances.forEach { assertTrue { it.isGround } }
            nonGroundBlocksInstances.forEach { assertFalse { it.isGround } }
        }

        test("empty-block-detected") {
            assertTrue(BlockImpl(null).isEmptyBlock)
        }

        test("to-list-return-value") {
            val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(mixedBlocks, mixedBlocksInstances.map { it.toList() }) { expectedList, actualList ->
                assertEquals(expectedList, actualList)
            }
        }

        test("to-array-return-value") {
            val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(
                mixedBlocks,
                mixedBlocksInstances.map { it.toArray().toList() },
            ) { expectedList, actualList ->
                assertEquals(expectedList, actualList)
            }
        }

        test("to-sequence-return-value") {
            val mixedBlocks = BlockUtils.mixedBlocks.map { it.toList() }

            onCorrespondingItems(
                mixedBlocks,
                mixedBlocksInstances.map { it.toSequence().toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            mixedBlocksInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            mixedBlocksInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            val blockWithSameVarName = BlockImpl(Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")))

            assertAllVsAll(blockWithSameVarName.args) { anElement, anotherElement ->
                assertEqualities(anElement, anotherElement)
                assertSame(anElement, anotherElement)
            }

            val blockCopied = blockWithSameVarName.freshCopy()
            assertAllVsAll(blockCopied.args, ::assertEqualities)
        }
    })
