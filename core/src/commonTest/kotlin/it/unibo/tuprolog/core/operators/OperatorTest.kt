package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.toTerm
import kotlin.test.*

/**
 * Test class for [Operator]
 *
 * @author Enrico
 */
internal class OperatorTest {

    private val plusFunctor = "+"
    private val plusAssociativity = Associativity.YFX
    private val plusPriority = 500

    private val plusOperator = Operator(plusFunctor, plusAssociativity, plusPriority)

    @Test
    fun functorCorrect() {
        assertEquals(plusFunctor, plusOperator.functor)
    }

    @Test
    fun associativityCorrect() {
        assertEquals(plusAssociativity, plusOperator.associativity)
    }

    @Test
    fun priorityCorrect() {
        assertEquals(plusPriority, plusOperator.priority)
    }

    @Test
    fun equalsWorksAsExpected() {
        val differentFunctor = plusFunctor + "x"
        val differentAssociativity = (Associativity.values().toSet() - plusAssociativity).first()
        val differentPriority = plusPriority + 1

        assertEquals(plusOperator, plusOperator)
        assertEquals(plusOperator, Operator(plusFunctor, plusAssociativity, differentPriority))

        assertNotEquals(plusOperator, Operator(differentFunctor, plusAssociativity, plusPriority))
        assertNotEquals(plusOperator, Operator(plusFunctor, differentAssociativity, plusPriority))
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToString = "Operator($plusPriority, $plusAssociativity, '$plusFunctor')"
        assertEquals(correctToString, plusOperator.toString())
    }

    @Test
    fun compareToUsesPriorityToCompute() {
        val lowOperator = Operator("operator", Associativity.YFX, 100)
        val mediumOperator = Operator("operator", Associativity.YFX, 200)
        val highOperator = Operator("operator", Associativity.YFX, 300)

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
        val correct = Struct.of(Operator.FUNCTOR, plusPriority.toTerm(), plusAssociativity.toTerm(), plusFunctor.toTerm())
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
        val toBeTested = Operator.fromTerm(Struct.of(Operator.FUNCTOR, plusPriority.toTerm(), plusAssociativity.toTerm(), plusFunctor.toTerm()))

        assertEquals(plusOperator, toBeTested)
        toBeTested?.run { assertTrue(plusOperator.compareTo(toBeTested) == 0) }
                ?: fail("Should not be null")
    }

    @Test
    fun fromTermReturnsNullIfNotCorrectOperator() {
        /** Utility function to help testing fromTerm method */
        fun testFromTerm(functor: String = Operator.FUNCTOR, priority: Term = plusPriority.toTerm(),
                         associativity: Term = plusAssociativity.toTerm(), opFunctor: Term = plusFunctor.toTerm()) =
                Operator.fromTerm(Struct.of(functor, priority, associativity, opFunctor))

        assertEquals(plusOperator, testFromTerm())

        assertEquals(null, Operator.fromTerm(Struct.of(Operator.FUNCTOR, plusAssociativity.toTerm())))
        assertEquals(null, testFromTerm(functor = "ciao"))
        assertEquals(null, testFromTerm(priority = plusFunctor.toTerm()))
        assertEquals(null, testFromTerm(priority = Var.anonymous()))
        assertEquals(null, testFromTerm(associativity = Var.anonymous()))
        assertEquals(null, testFromTerm(associativity = "a".toTerm()))
        assertEquals(null, testFromTerm(opFunctor = Var.anonymous()))
    }
}