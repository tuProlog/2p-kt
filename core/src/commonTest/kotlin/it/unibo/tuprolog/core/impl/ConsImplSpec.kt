package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertFalse
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConsUtils
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [ConsImpl] and [Cons]
 *
 * @author Enrico
 */
class ConsImplSpec :
    FunSpec({
        val consInstances = ConsUtils.mixedConsInstances(::ConsImpl)

        val consInstancesHeads = ConsUtils.mixedConsInstancesHeads

        val consInstancesTails = ConsUtils.mixedConsInstancesTails(::ConsImpl)

        val consInstancesElementLists = ConsUtils.mixedConsInstancesElementLists

        val consInstancesUnfoldedLists = ConsUtils.mixedConsInstancesUnfoldedLists

        test("cons-functor") {
            consInstances.forEach { assertEquals(".", it.functor) }
        }

        test("head-correct") {
            onCorrespondingItems(consInstancesHeads, consInstances.map { it.head }, ::assertEqualities)
        }

        test("tail-correct") {
            onCorrespondingItems(consInstancesTails, consInstances.map { it.tail }, ::assertEqualities)
        }

        test("args-correct") {
            val consInstancesArgs =
                consInstancesHeads.zip(consInstancesTails).map { (head, tail) ->
                    listOf(head, tail)
                }

            onCorrespondingItems(consInstancesArgs, consInstances.map { it.args }) { expectedArgs, actualArgs ->
                assertEquals(expectedArgs, actualArgs)
            }
        }

        test("unfolded-list-correct") {
            onCorrespondingItems(
                consInstancesUnfoldedLists,
                consInstances.map { it.unfoldedList },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("unfolded-sequence-correct") {
            onCorrespondingItems(
                consInstancesUnfoldedLists,
                consInstances.map { it.unfoldedSequence.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("unfolded-array-correct") {
            onCorrespondingItems(
                consInstancesUnfoldedLists,
                consInstances.map { it.unfoldedArray.toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-list-return-value") {
            onCorrespondingItems(
                consInstancesElementLists,
                consInstances.map { it.toList() },
            ) { expectedList, actualList ->
                assertEquals(expectedList, actualList)
            }
        }

        test("to-array-return-value") {
            onCorrespondingItems(
                consInstancesElementLists,
                consInstances.map { it.toArray().toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-sequence-return-value") {
            onCorrespondingItems(
                consInstancesElementLists,
                consInstances.map { it.toSequence().toList() },
            ) { expectedList, actualList -> assertEquals(expectedList, actualList) }
        }

        test("to-string-works-as-expected") {
            val correctToString = ConsUtils.mixedConsInstancesCorrectToString

            onCorrespondingItems(correctToString, consInstances.map { it.toString() }) { expectedString, actualString ->
                assertEquals(expectedString, actualString)
            }
        }

        test("size-correct") {
            val correctSizes = consInstancesElementLists.map { it.size }

            onCorrespondingItems(correctSizes, consInstances.map { it.size }) { expectedSize, actualSize ->
                assertEquals(expectedSize, actualSize)
            }
        }

        test("is-ground-true-only-if-no-variables-are-present") {
            assertFalse(
                ConsUtils.oneElementList(::ConsImpl).isGround,
                ConsUtils.twoElementList(::ConsImpl).isGround,
                ConsUtils.twoElementListWithPipe(::ConsImpl).isGround,
                ConsUtils.threeElementListWithPipe(::ConsImpl).isGround,
            )

            assertTrue(ConsUtils.threeElementList(::ConsImpl).isGround)
        }

        test("is-properties-and-types") {
            consInstances.forEach(TermTypeAssertionUtils::assertIsCons)
        }

        test("two-arity") {
            consInstances.forEach { assertEquals(it.arity, 2) }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            consInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            consInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::ConsImpl)
        }
    })
