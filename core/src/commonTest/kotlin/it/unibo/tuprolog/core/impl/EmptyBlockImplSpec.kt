package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [EmptyBlockImpl] and [EmptyBlock]
 *
 * @author Enrico
 */
class EmptyBlockImplSpec :
    FunSpec({
        val testedObj = EmptyBlockImpl()

        test("empty-block-functor") {
            testedObj.functor shouldBe "{}"
        }

        test("empty-arguments") {
            AtomUtils.assertNoArguments(testedObj)
        }

        test("is-properties-and-types") {
            TermTypeAssertionUtils.assertIsEmptyBlock(testedObj)
        }

        test("variables-empty") {
            testedObj.variables.none().shouldBeTrue()
        }

        test("functor-does-not-respect-struct-regex") {
            testedObj.isFunctorWellFormed.shouldBeFalse()
        }

        test("empty-block-fresh-copy-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyIsItself(testedObj)
        }

        test("empty-block-fresh-copy-with-scope-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
        }
    })
