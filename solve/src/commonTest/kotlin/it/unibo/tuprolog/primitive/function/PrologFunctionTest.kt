package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [PrologFunction.Companion]
 *
 * @author Enrico
 */
internal class PrologFunctionTest {

    private val anAtom = Atom.of("a")
    private val aContext = DummyInstances.executionContext

    @Test
    fun ofNullaryExecutesPassedFunction() {
        val nullaryFunction1 = PrologFunction.ofNullary { throw IllegalStateException() }
        val nullaryFunction2 = PrologFunction.ofNullary { anAtom }

        assertFailsWith<IllegalStateException> { nullaryFunction1(aContext) }
        assertEquals(anAtom, nullaryFunction2(aContext))
    }

    @Test
    fun ofUnaryPassesCorrectlyParameters() {
        val unaryFunction = PrologFunction.ofUnary { arg, _ -> arg.also { assertEquals(anAtom, arg) } }

        assertEquals(anAtom, unaryFunction(anAtom, aContext))
    }

    @Test
    fun ofBinaryPassesCorrectlyParameters() {
        val testArg2 = Atom.of("arg2")

        val binaryFunction = PrologFunction.ofBinary { arg1, arg2, _ -> arg1.also { assertEquals(anAtom, arg1); assertEquals(testArg2, arg2) } }

        assertEquals(anAtom, binaryFunction(anAtom, testArg2, aContext))
    }

}
