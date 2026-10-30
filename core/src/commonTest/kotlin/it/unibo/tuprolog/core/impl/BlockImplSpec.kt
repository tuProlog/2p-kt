package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertAllVsAll
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
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
        fun blocks(
            property: String,
            cases: List<BlockUtils.BlockCase> = BlockUtils.mixedBlockCases,
            test: (BlockUtils.BlockCase, Block) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, cases) { test(it, BlockImpl(it.tupleWrapped)) }

        blocks("functor") { _, block -> assertEquals("{}", block.functor) }

        blocks("args") { case, block -> assertEqualities(case.tupleWrapped, block.args.first()) }

        blocks("unfolded-list") { case, block -> assertEquals(case.elements, block.unfoldedList) }

        blocks("unfolded-sequence") { case, block -> assertEquals(case.elements, block.unfoldedSequence.toList()) }

        blocks("unfolded-array") { case, block -> assertEquals(case.elements, block.unfoldedArray.toList()) }

        blocks("to-string") { case, block -> assertEquals("{${case.elements.joinToString(", ")}}", block.toString()) }

        blocks("type-checks") { _, block -> TermTypeAssertionUtils.assertIsBlock(block) }

        blocks("is-ground", BlockUtils.groundBlockCases) { _, block -> assertTrue { block.isGround } }

        blocks("is-not-ground", BlockUtils.nonGroundBlockCases) { _, block -> assertFalse { block.isGround } }

        test("empty-block-detected") {
            assertTrue(BlockImpl(null).isEmptyBlock)
        }

        blocks("to-list") { case, block -> assertEquals(case.elements, block.toList()) }

        blocks("to-array") { case, block -> assertEquals(case.elements, block.toArray().toList()) }

        blocks("to-sequence") { case, block -> assertEquals(case.elements, block.toSequence().toList()) }

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
                assertSame(anElement, anotherElement)
            }

            val blockCopied = blockWithSameVarName.freshCopy()
            assertAllVsAll(blockCopied.args, ::assertEqualities)
        }
    })
