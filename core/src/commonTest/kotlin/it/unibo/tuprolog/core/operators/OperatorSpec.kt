package it.unibo.tuprolog.core.operators

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.toTerm

/**
 * Test class for [Operator]
 *
 * @author Enrico
 */
class OperatorSpec :
    FunSpec({
        val plusFunctor = "+"

        val plusSpecifier = Specifier.YFX

        val plusPriority = 500

        val plusOperator = Operator(plusFunctor, plusSpecifier, plusPriority)

        test("functor-correct") {
            plusOperator.functor shouldBe plusFunctor
        }

        test("specifier-correct") {
            plusOperator.specifier shouldBe plusSpecifier
        }

        test("priority-correct") {
            plusOperator.priority shouldBe plusPriority
        }

        test("equals-works-as-expected") {
            val differentFunctor = plusFunctor + "x"
            val differentSpecifier = (Specifier.values().toSet() - plusSpecifier).first()
            val differentPriority = plusPriority + 1

            plusOperator shouldBe plusOperator
            Operator(plusFunctor, plusSpecifier, differentPriority) shouldBe plusOperator

            Operator(differentFunctor, plusSpecifier, plusPriority) shouldNotBe plusOperator
            Operator(plusFunctor, differentSpecifier, plusPriority) shouldNotBe plusOperator
        }

        test("to-string-works-as-expected") {
            val correctToString = "Operator($plusPriority, $plusSpecifier, '$plusFunctor')"
            plusOperator.toString() shouldBe correctToString
        }

        test("compare-to-uses-priority-to-compute") {
            val lowOperator = Operator("operator", Specifier.YFX, 100)
            val mediumOperator = Operator("operator", Specifier.YFX, 200)
            val highOperator = Operator("operator", Specifier.YFX, 300)

            (lowOperator < mediumOperator).shouldBeTrue()
            (mediumOperator < highOperator).shouldBeTrue()
            (lowOperator < highOperator).shouldBeTrue()

            (lowOperator > mediumOperator).shouldBeFalse()
            (mediumOperator > highOperator).shouldBeFalse()
            (lowOperator > highOperator).shouldBeFalse()

            (lowOperator >= lowOperator).shouldBeTrue()
            (lowOperator <= lowOperator).shouldBeTrue()
        }

        test("to-term-works-as-expected") {
            val correct =
                Struct.of(
                    Operator.FUNCTOR,
                    plusPriority.toTerm(),
                    plusSpecifier.toTerm(),
                    plusFunctor.toTerm(),
                )
            val toBeTested = plusOperator.toTerm()

            assertEqualities(correct, toBeTested)
        }

        test("template-correct") {
            assertStructurallyEquals(
                Operator.TEMPLATE,
                Struct.of(Operator.FUNCTOR, Var.anonymous(), Var.anonymous(), Var.anonymous()),
            )
        }

        test("from-term-parses-correct-operator") {
            val toBeTested =
                Operator.fromTerm(
                    Struct.of(
                        Operator.FUNCTOR,
                        plusPriority.toTerm(),
                        plusSpecifier.toTerm(),
                        plusFunctor.toTerm(),
                    ),
                )

            toBeTested shouldBe plusOperator
            toBeTested?.run { plusOperator.compareTo(toBeTested) shouldBe 0 }
                ?: fail("Should not be null")
        }

        test("from-term-returns-null-if-not-correct-operator") {
            /** Utility function to help testing fromTerm method */
            fun testFromTerm(
                functor: String = Operator.FUNCTOR,
                priority: Term = plusPriority.toTerm(),
                specifier: Term = plusSpecifier.toTerm(),
                opFunctor: Term = plusFunctor.toTerm(),
            ) = Operator.fromTerm(Struct.of(functor, priority, specifier, opFunctor))

            testFromTerm() shouldBe plusOperator

            Operator.fromTerm(Struct.of(Operator.FUNCTOR, plusSpecifier.toTerm())).shouldBeNull()
            testFromTerm(functor = "ciao").shouldBeNull()
            testFromTerm(priority = plusFunctor.toTerm()).shouldBeNull()
            testFromTerm(priority = Var.anonymous()).shouldBeNull()
            testFromTerm(specifier = Var.anonymous()).shouldBeNull()
            testFromTerm(specifier = "a".toTerm()).shouldBeNull()
            testFromTerm(opFunctor = Var.anonymous()).shouldBeNull()
        }

        test("compare-to/same-priority-orders-by-specifier") {
            val yfx = Operator("+", Specifier.YFX, 500)
            val xfx = Operator("+", Specifier.XFX, 500)
            yfx.compareTo(xfx) shouldBe yfx.specifier.compareTo(xfx.specifier)
            yfx.compareTo(xfx) shouldNotBe 0
        }
    })
