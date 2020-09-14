package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.toTerm
import kotlin.test.Test
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
internal class OperatorTest {

    private val plusFunctor = "+"
    private val plusSpecifier = Specifier.YFX
    private val plusPriority = 500

    private val plusOperator = Operator(plusFunctor, plusSpecifier, plusPriority)

    @Test
    fun functorCorrect() {
        assertEquals(plusFunctor, plusOperator.functor)
    }

    @Test
    fun specifierCorrect() {
        assertEquals(plusSpecifier, plusOperator.specifier)
    }

    @Test
    fun priorityCorrect() {
        assertEquals(plusPriority, plusOperator.priority)
    }

    @Test
    fun equalsWorksAsExpected() {
        val differentFunctor = plusFunctor + "x"
        val differentSpecifier = (Specifier.values().toSet() - plusSpecifier).first()
        val differentPriority = plusPriority + 1

        assertEquals(plusOperator, plusOperator)
        assertEquals(plusOperator, Operator(plusFunctor, plusSpecifier, differentPriority))

        assertNotEquals(plusOperator, Operator(differentFunctor, plusSpecifier, plusPriority))
        assertNotEquals(plusOperator, Operator(plusFunctor, differentSpecifier, plusPriority))
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToString = "Operator($plusPriority, $plusSpecifier, '$plusFunctor')"
        assertEquals(correctToString, plusOperator.toString())
    }

    @Test
    fun compareToUsesPriorityToCompute() {
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

    @Test
    fun toTermWorksAsExpected() {
        val correct = Struct.of(Operator.FUNCTOR, plusPriority.toTerm(), plusSpecifier.toTerm(), plusFunctor.toTerm())
        val toBeTested = plusOperator.toTerm()

        assertEqualities(correct, toBeTested)
    }

    @Test
    fun templateCorrect() {
        assertStructurallyEquals(
            Operator.TEMPLATE,
            Struct.of(Operator.FUNCTOR, Var.anonymous(), Var.anonymous(), Var.anonymous())
        )
    }

    @Test
    fun fromTermParsesCorrectOperator() {
        val toBeTested = Operator.fromTerm(
            Struct.of(
                Operator.FUNCTOR,
                plusPriority.toTerm(),
                plusSpecifier.toTerm(),
                plusFunctor.toTerm()
            )
        )

        assertEquals(plusOperator, toBeTested)
        toBeTested?.run { assertTrue(plusOperator.compareTo(toBeTested) == 0) }
            ?: fail("Should not be null")
    }

    @Test
    fun fromTermReturnsNullIfNotCorrectOperator() {
        /** Utility function to help testing fromTerm method */
        fun testFromTerm(
            functor: String = Operator.FUNCTOR,
            priority: Term = plusPriority.toTerm(),
            specifier: Term = plusSpecifier.toTerm(),
            opFunctor: Term = plusFunctor.toTerm()
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
}
