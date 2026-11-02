package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.BlockUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [BlockImpl] and [Block]
 *
 * @author Enrico
 */
class BlockImplSpec :
    FunSpec({
        fun blocks(
            property: String,
            cases: List<BlockUtils.BlockCase> = BlockUtils.mixedBlockCases,
            test: (BlockUtils.BlockCase, Block) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, BlockImpl(it.tupleWrapped)) }

        blocks("functor") { _, block -> block.functor shouldBe "{}" }

        blocks("args") { case, block -> assertEqualities(case.tupleWrapped, block.args.first()) }

        blocks("unfolded-list") { case, block -> block.unfoldedList shouldBe case.elements }

        blocks("unfolded-sequence") { case, block -> block.unfoldedSequence.toList() shouldBe case.elements }

        blocks("unfolded-array") { case, block -> block.unfoldedArray.toList() shouldBe case.elements }

        blocks("to-string") { case, block -> block.toString() shouldBe "{${case.elements.joinToString(", ")}}" }

        blocks("type-checks") { _, block -> TermTypeAssertionUtils.assertIsBlock(block) }

        blocks("is-ground", BlockUtils.groundBlockCases) { _, block -> block.isGround.shouldBeTrue() }

        blocks("is-not-ground", BlockUtils.nonGroundBlockCases) { _, block -> block.isGround.shouldBeFalse() }

        test("unfold/yields-the-block-then-each-nested-tuple-then-the-last-item") {
            val (a, b, c) = listOf("a", "b", "c").map { Atom.of(it) }
            val block = Block.of(a, b, c)
            block.unfold().toList() shouldBe listOf(block, Tuple.of(a, b, c), Tuple.of(b, c), c)
            Block.of(a).unfold().toList() shouldBe listOf(Block.of(a), a)
            Block.empty().unfold().toList() shouldBe listOf(Block.empty())
        }

        test("empty-block-detected") {
            BlockImpl(null).isEmptyBlock.shouldBeTrue()
        }

        blocks("to-list") { case, block -> block.toList() shouldBe case.elements }

        blocks("to-array") { case, block -> block.toArray().toList() shouldBe case.elements }

        blocks("to-sequence") { case, block -> block.toSequence().toList() shouldBe case.elements }

        blocks("fresh-copy-is-itself", BlockUtils.groundBlockCases) { _, block ->
            ConstantUtils.assertFreshCopyIsItself(block)
        }

        blocks("fresh-copy-renews-variables", BlockUtils.nonGroundBlockCases) { _, block ->
            StructUtils.assertFreshCopyRenewsContainedVariables(block)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            val blockWithSameVarName = BlockImpl(Tuple.of(Var.of("A"), Var.of("A"), Var.of("A")))

            assertAllVsAll(blockWithSameVarName.args) { anElement, anotherElement ->
                assertEqualities(anElement, anotherElement)
                anotherElement shouldBeSameInstanceAs anElement
            }

            val blockCopied = blockWithSameVarName.freshCopy()
            assertAllVsAll(blockCopied.args, ::assertEqualities)
        }
    })
