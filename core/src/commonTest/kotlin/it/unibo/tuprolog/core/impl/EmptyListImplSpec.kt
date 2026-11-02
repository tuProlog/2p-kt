package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [EmptyListImpl] and [EmptyList]
 *
 * @author Enrico
 */
class EmptyListImplSpec :
    FunSpec({
        val testedObj = EmptyListImpl()

        test("empty-list-functor") {
            testedObj.functor shouldBe "[]"
        }

        test("unfolded-list-contains-empty-list") {
            testedObj.unfoldedList shouldBe listOf(EmptyListImpl())
        }

        test("unfolded-sequence-contains-empty-list") {
            testedObj.unfoldedSequence.toList() shouldBe sequenceOf(EmptyListImpl()).toList()
        }

        test("unfolded-array-contains-empty-list") {
            arrayOf(EmptyListImpl()).contentDeepEquals(testedObj.unfoldedArray).shouldBeTrue()
        }

        test("to-array-return-value") {
            testedObj.toArray().isEmpty().shouldBeTrue()
        }

        test("to-sequence-return-value") {
            testedObj
                .toSequence()
                .toList()
                .isEmpty()
                .shouldBeTrue()
        }

        test("to-list-return-value") {
            testedObj.toList().isEmpty().shouldBeTrue()
        }

        test("to-string-should-work-as-expected") {
            testedObj.toString() shouldBe "[]"
        }

        test("size-should-be-zero") {
            testedObj.size shouldBe 0
        }

        test("is-properties-and-types") {
            TermTypeAssertionUtils.assertIsEmptyList(testedObj)
        }

        test("functor-does-not-respect-struct-regex") {
            testedObj.isFunctorWellFormed.shouldBeFalse()
        }

        test("empty-list-fresh-copy-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyIsItself(testedObj)
        }

        test("empty-list-fresh-copy-with-scope-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
        }
    })
