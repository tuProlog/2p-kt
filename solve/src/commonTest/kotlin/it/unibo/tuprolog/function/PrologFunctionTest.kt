package it.unibo.tuprolog.function

import it.unibo.tuprolog.core.Atom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [PrologFunction.Companion]
 *
 * @author Enrico
 */
internal class PrologFunctionTest {

    private val testAtom = Atom.of("a")

    @Test
    fun ofNullaryExecutesPassedFunction() {
        val nullaryFunction1 = PrologFunction.ofNullary { throw IllegalStateException() }
        val nullaryFunction2 = PrologFunction.ofNullary { testAtom }

        assertFailsWith<IllegalStateException> { nullaryFunction1() }
        assertEquals(testAtom, nullaryFunction2())
    }

    @Test
    fun ofUnaryPassesCorrectlyParameters() {
        val unaryFunction = PrologFunction.ofUnary { arg -> arg.also { assertEquals(testAtom, arg) } }

        assertEquals(testAtom, unaryFunction(testAtom))
    }

    @Test
    fun ofBinaryPassesCorrectlyParameters() {
        val testArg2 = Atom.of("arg2")

        val binaryFunction = PrologFunction.ofBinary { arg1, arg2 -> arg1.also { assertEquals(testAtom, arg1); assertEquals(testArg2, arg2) } }

        assertEquals(testAtom, binaryFunction(testAtom, testArg2))
    }

}
