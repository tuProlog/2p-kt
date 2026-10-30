package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.TupleUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [TupleImpl] and [Tuple]
 *
 * @author Enrico
 */
class TupleImplSpec :
    FunSpec({
        val twoElementsTuple = TupleUtils.twoElementTuple(::TupleImpl)

        val threeElementsTuple = TupleUtils.threeElementTuple(::TupleImpl)

        val tupleInstances = TupleUtils.tupleInstances(::TupleImpl)

        val tupleInstancesLefts = TupleUtils.tupleInstancesLefts

        val tupleInstancesRights = TupleUtils.tupleInstancesRights(::TupleImpl)

        val tupleInstancesElementLists = TupleUtils.tupleInstancesElementLists

        test("tuple-functor") {
            tupleInstances.forEach { assertEquals(",", it.functor) }
        }

        test("left-correct") {
            onCorrespondingItems(tupleInstancesLefts, tupleInstances.map { it.left }, ::assertEqualities)
        }

        test("right-correct") {
            onCorrespondingItems(tupleInstancesRights, tupleInstances.map { it.right }, ::assertEqualities)
        }

        test("args-correct") {
            val tupleInstancesArgs =
                tupleInstancesLefts.zip(tupleInstancesRights).map { (left, right) -> listOf(left, right) }

            onCorrespondingItems(tupleInstancesArgs, tupleInstances.map { it.args }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("unfolded-list-correct") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.unfoldedList },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("unfolded-sequence-correct") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.unfoldedSequence.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("unfolded-array-correct") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.unfoldedArray.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-list-return-value") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-array-return-value") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.toArray().toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-sequence-return-value") {
            onCorrespondingItems(
                tupleInstancesElementLists,
                tupleInstances.map { it.toSequence().toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-string-works-as-expected") {
            val tupleToStrings = tupleInstancesElementLists.map { it.joinToString(", ", "(", ")") }

            onCorrespondingItems(tupleToStrings, tupleInstances.map { it.toString() }) { expectedList, actualList ->
                assertEquals(expectedList, actualList)
            }
        }

        test("is-properties-and-types") {
            tupleInstances.forEach(TermTypeAssertionUtils::assertIsTuple)
        }

        test("is-ground-true-only-if-no-variables-are-present") {
            assertFalse(twoElementsTuple.isGround)
            assertTrue(threeElementsTuple.isGround)
        }

        test("two-arity") {
            tupleInstances.forEach { assertEquals(2, it.arity) }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            tupleInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            tupleInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::TupleImpl)
        }
    })
