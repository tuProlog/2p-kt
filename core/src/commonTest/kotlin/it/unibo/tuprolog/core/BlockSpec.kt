package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.BlockImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.BlockUtils
import kotlin.test.assertEquals

/**
 * Test class for [Block] companion object
 *
 * @author Enrico
 */
class BlockSpec :
    FunSpec({
        test("empty-returns-empty-block") {
            assertEqualities(Empty.block(), Block.empty())
            assertEquals(Empty.block(), Block.empty())
        }

        test("block-of-no-vararg-terms") {
            assertEqualities(Empty.block(), Block.of())
        }

        withTests(nameFn = { "of-varargs/${it.id}" }, BlockUtils.mixedBlockCases) { case ->
            assertEqualities(BlockImpl(case.tupleWrapped), Block.of(*case.elements.toTypedArray()))
        }

        test("block-of-empty-iterable-of-terms") {
            assertEqualities(Empty.block(), Block.of(emptyList<Term>().asIterable()))
        }

        withTests(nameFn = { "of-iterable/${it.id}" }, BlockUtils.mixedBlockCases) { case ->
            assertEqualities(BlockImpl(case.tupleWrapped), Block.of(case.elements.toList().asIterable()))
        }

        test("block-of-empty-sequence-of-terms") {
            assertEqualities(Empty.block(), Block.of(emptySequence()))
        }

        withTests(nameFn = { "of-sequence/${it.id}" }, BlockUtils.mixedBlockCases) { case ->
            assertEqualities(BlockImpl(case.tupleWrapped), Block.of(case.elements.asSequence()))
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
