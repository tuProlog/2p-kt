package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.BlockImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.BlockUtils

/**
 * Test class for [Block] companion object
 *
 * @author Enrico
 */
class BlockSpec :
    FunSpec({
        test("empty-returns-empty-block") {
            assertEqualities(Empty.block(), Block.empty())
            Block.empty() shouldBe Empty.block()
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

            block.toString() shouldBe nums.joinToString(", ", "{", "}")

            val otherBlock = block.freshCopy()

            otherBlock.hashCode() shouldBe block.hashCode()
            otherBlock shouldBe block
        }
    })
