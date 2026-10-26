package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [EmptyListImpl] and [EmptyList]
 *
 * @author Enrico
 */
class EmptyListImplSpec :
    FunSpec({
        val testedObj = EmptyListImpl()

        test("empty-list-functor") {
            assertEquals("[]", testedObj.functor)
        }

        test("unfolded-list-contains-empty-list") {
            assertEquals(listOf(EmptyListImpl()), testedObj.unfoldedList)
        }

        test("unfolded-sequence-contains-empty-list") {
            assertEquals(sequenceOf(EmptyListImpl()).toList(), testedObj.unfoldedSequence.toList())
        }

        test("unfolded-array-contains-empty-list") {
            assertTrue(arrayOf(EmptyListImpl()).contentDeepEquals(testedObj.unfoldedArray))
        }

        test("to-array-return-value") {
            assertTrue(testedObj.toArray().isEmpty())
        }

        test("to-sequence-return-value") {
            assertTrue(testedObj.toSequence().toList().isEmpty())
        }

        test("to-list-return-value") {
            assertTrue(testedObj.toList().isEmpty())
        }

        test("to-string-should-work-as-expected") {
            assertEquals("[]", testedObj.toString())
        }

        test("size-should-be-zero") {
            assertEquals(0, testedObj.size)
        }

        test("is-properties-and-types") {
            TermTypeAssertionUtils.assertIsEmptyList(testedObj)
        }

        test("functor-does-not-respect-struct-regex") {
            assertFalse(testedObj.isFunctorWellFormed)
        }

        test("empty-list-fresh-copy-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyIsItself(testedObj)
        }

        test("empty-list-fresh-copy-with-scope-should-return-the-instance-itself") {
            ConstantUtils.assertFreshCopyWithScopeIsItself(testedObj)
        }
    })
