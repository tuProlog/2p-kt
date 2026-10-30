package it.unibo.tuprolog.core.operators

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.toTerm
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

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
            assertEquals(plusFunctor, plusOperator.functor)
        }

        test("specifier-correct") {
            assertEquals(plusSpecifier, plusOperator.specifier)
        }

        test("priority-correct") {
            assertEquals(plusPriority, plusOperator.priority)
        }

        test("equals-works-as-expected") {
            val differentFunctor = plusFunctor + "x"
            val differentSpecifier = (Specifier.values().toSet() - plusSpecifier).first()
            val differentPriority = plusPriority + 1

            assertEquals(plusOperator, plusOperator)
            assertEquals(plusOperator, Operator(plusFunctor, plusSpecifier, differentPriority))

            assertNotEquals(plusOperator, Operator(differentFunctor, plusSpecifier, plusPriority))
            assertNotEquals(plusOperator, Operator(plusFunctor, differentSpecifier, plusPriority))
        }

        test("to-string-works-as-expected") {
            val correctToString = "Operator($plusPriority, $plusSpecifier, '$plusFunctor')"
            assertEquals(correctToString, plusOperator.toString())
        }

        test("compare-to-uses-priority-to-compute") {
            val lowOperator = Operator("operator", Specifier.YFX, 100)
            val mediumOperator = Operator("operator", Specifier.YFX, 200)
            val highOperator = Operator("operator", Specifier.YFX, 300)

            assertTrue { lowOperator < mediumOperator }
            assertTrue { mediumOperator < highOperator }
            assertTrue { lowOperator < highOperator }

            assertFalse { lowOperator > mediumOperator }
            assertFalse { mediumOperator > highOperator }
            assertFalse { lowOperator > highOperator }

            assertTrue { lowOperator >= lowOperator }
            assertTrue { lowOperator <= lowOperator }
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

            assertEquals(plusOperator, toBeTested)
            toBeTested?.run { assertTrue(plusOperator.compareTo(toBeTested) == 0) }
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

            assertEquals(plusOperator, testFromTerm())

            assertNull(Operator.fromTerm(Struct.of(Operator.FUNCTOR, plusSpecifier.toTerm())))
            assertNull(testFromTerm(functor = "ciao"))
            assertNull(testFromTerm(priority = plusFunctor.toTerm()))
            assertNull(testFromTerm(priority = Var.anonymous()))
            assertNull(testFromTerm(specifier = Var.anonymous()))
            assertNull(testFromTerm(specifier = "a".toTerm()))
            assertNull(testFromTerm(opFunctor = Var.anonymous()))
        }

        test("compare-to/same-priority-orders-by-specifier") {
            val yfx = Operator("+", Specifier.YFX, 500)
            val xfx = Operator("+", Specifier.XFX, 500)
            assertEquals(yfx.specifier.compareTo(xfx.specifier), yfx.compareTo(xfx))
            assertNotEquals(0, yfx.compareTo(xfx))
        }
    })
