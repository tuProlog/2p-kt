package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.BlockImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.BlockUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Block] companion object
 *
 * @author Enrico
 */
internal class BlockTest {

    private val correctInstances = BlockUtils.mixedBlocksTupleWrapped.map(::BlockImpl)

    @Test
    fun emptyReturnsEmptyBlock() {
        assertEqualities(Empty.block(), Block.empty())
        assertEquals(Empty.block(), Block.empty())
    }

    @Test
    fun blockOfNoVarargTerms() {
        assertEqualities(Empty.block(), Block.of())
    }

    @Test
    fun blockOfVarargTerms() {
        val toBeTested = BlockUtils.mixedBlocks.map { Block.of(*it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun blockOfEmptyIterableOfTerms() {
        assertEqualities(Empty.block(), Block.of(emptyList<Term>().asIterable()))
    }

    @Test
    fun blockOfIterableOfTerms() {
        val toBeTested = BlockUtils.mixedBlocks.map { Block.of(it.toList().asIterable()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun blockOfEmptySequenceOfTerms() {
        assertEqualities(Empty.block(), Block.of(emptySequence()))
    }

    @Test
    fun blockOfSequenceOfTerms() {
        val toBeTested = BlockUtils.mixedBlocks.map { Block.of(it.asSequence()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun bigBlocksDoNotProvokeStackOverflow() {
        val nums = (0..100_000).toList()
        val block = Block.of(nums.map { Integer.of(it) })

        assertEquals(nums.joinToString(", ", "{", "}"), block.toString())

        val otherBlock = block.freshCopy()

        assertEquals(block.hashCode(), otherBlock.hashCode())
        assertEquals(block, otherBlock)
    }
}
