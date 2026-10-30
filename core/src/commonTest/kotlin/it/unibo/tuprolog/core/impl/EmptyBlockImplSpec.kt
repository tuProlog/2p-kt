package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [EmptyBlockImpl] and [EmptyBlock]
 *
 * @author Enrico
 */
class EmptyBlockImplSpec :
    FunSpec({
        val testedObj = EmptyBlockImpl()

        test("empty-block-functor") {
            assertEquals("{}", testedObj.functor)
        }

        test("empty-arguments") {
            AtomUtils.assertNoArguments(testedObj)
        }

        test("is-properties-and-types") {
            TermTypeAssertionUtils.assertIsEmptyBlock(testedObj)
        }

        test("variables-empty") {
            assertTrue { testedObj.variables.none() }
        }

        test("functor-does-not-respect-struct-regex") {
            assertFalse(testedObj.isFunctorWellFormed)
        }

        test("empty-block-fresh-copy-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyIsItself(testedObj)
        }

        test("empty-block-fresh-copy-with-scope-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
        }
    })
