package it.unibo.tuprolog.core.operators

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.operators.testutils.OperatorSetUtils

/**
 * Test class for [OperatorSet]
 *
 * @author Enrico
 */
class OperatorSetSpec :
    FunSpec({
        val fakePlusOperator = Operator("+", Specifier.YFX, 500)

        val fakeMinusOperator = Operator("-", Specifier.YFX, 500)

        val fakeTimesOperator = Operator("*", Specifier.YFX, 400)

        val fakeDivisionOperator = Operator("/", Specifier.YFX, 400)

        val operatorSet = OperatorSet(fakePlusOperator, fakeMinusOperator, fakeTimesOperator, fakeDivisionOperator)

        val overridingFakePlusOperator = Operator("+", Specifier.YFX, 1000)

        val overridingOperatorSet = OperatorSet(overridingFakePlusOperator)

        test("constructing-operator-set-with-equals-operators-takes-only-first") {
            val toBeTested = OperatorSet(fakePlusOperator, overridingFakePlusOperator)

            (toBeTested.any { it === fakePlusOperator }).shouldBeTrue()
            (toBeTested.none { it === overridingFakePlusOperator }).shouldBeTrue()
        }

        test("plus-operator") {
            val toBeTested = operatorSet + overridingFakePlusOperator

            (toBeTested.any { it === overridingFakePlusOperator }).shouldBeTrue()
            (toBeTested.none { it === fakePlusOperator }).shouldBeTrue()
        }

        test("plus-operator-set") {
            val toBeTested = operatorSet + overridingOperatorSet

            (toBeTested.any { it === overridingFakePlusOperator }).shouldBeTrue()
            (toBeTested.none { it === fakePlusOperator }).shouldBeTrue()
        }

        test("minus-operator") {
            val toBeTested = operatorSet - fakePlusOperator

            (toBeTested.none { it === fakePlusOperator }).shouldBeTrue()
            (toBeTested.none { it === overridingFakePlusOperator }).shouldBeTrue()
        }

        test("minus-operator-set") {
            val toBeTested = operatorSet - OperatorSet(fakePlusOperator)

            (toBeTested.none { it === fakePlusOperator }).shouldBeTrue()
            (toBeTested.none { it === overridingFakePlusOperator }).shouldBeTrue()
        }

        test("same-contents-operator-sets-are-equals") {
            OperatorSet() shouldBe OperatorSet()
            OperatorSet(fakePlusOperator, fakeMinusOperator, fakeTimesOperator, fakeDivisionOperator) shouldBe
                operatorSet
            OperatorSet(fakeMinusOperator, fakePlusOperator, fakeDivisionOperator, fakeTimesOperator) shouldBe
                operatorSet

            OperatorSet() shouldNotBe operatorSet
        }

        test("predefined-operators-count") {
            OperatorSetUtils.defaultOperatorCases.count() shouldBe OperatorSet.DEFAULT.count()
        }

        withTests(nameFn = { "predefined-operators/${it.id}" }, OperatorSetUtils.defaultOperatorCases) { case ->
            // expectedOperator should be present in OperatorSet
            val actualOperator =
                OperatorSet.DEFAULT.single { it == Operator(case.functor, case.specifier, case.priority) }

            withClue("$actualOperator functor expected to be: ${case.functor}") {
                actualOperator.functor shouldBe
                    case.functor
            }
            withClue("$actualOperator specifier expected to be: ${case.specifier}") {
                actualOperator.specifier shouldBe
                    case.specifier
            }
            withClue("$actualOperator priority expected to be: ${case.priority}") {
                actualOperator.priority shouldBe
                    case.priority
            }
        }
    })
