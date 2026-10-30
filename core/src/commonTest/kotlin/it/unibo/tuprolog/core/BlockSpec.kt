package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.BlockImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.BlockUtils
import kotlin.test.assertEquals

/**
 * Test class for [Block] companion object
 *
 * @author Enrico
 */
class BlockSpec :
    FunSpec({
        val correctInstances = BlockUtils.mixedBlocksTupleWrapped.map(::BlockImpl)

        test("empty-returns-empty-block") {
            assertEqualities(Empty.block(), Block.empty())
            assertEquals(Empty.block(), Block.empty())
        }

        test("block-of-no-vararg-terms") {
            assertEqualities(Empty.block(), Block.of())
        }

        test("block-of-vararg-terms") {
            val toBeTested = BlockUtils.mixedBlocks.map { Block.of(*it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("block-of-empty-iterable-of-terms") {
            assertEqualities(Empty.block(), Block.of(emptyList<Term>().asIterable()))
        }

        test("block-of-iterable-of-terms") {
            val toBeTested = BlockUtils.mixedBlocks.map { Block.of(it.toList().asIterable()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("block-of-empty-sequence-of-terms") {
            assertEqualities(Empty.block(), Block.of(emptySequence()))
        }

        test("block-of-sequence-of-terms") {
            val toBeTested = BlockUtils.mixedBlocks.map { Block.of(it.asSequence()) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("big-blocks-do-not-provoke-stack-overflow") {
            val nums = (0..100_000).toList()
            val block = Block.of(nums.map { Integer.of(it) })

            assertEquals(nums.joinToString(", ", "{", "}"), block.toString())

            val otherBlock = block.freshCopy()

            assertEquals(block.hashCode(), otherBlock.hashCode())
            assertEquals(block, otherBlock)
        }
    })
