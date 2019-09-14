package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [PredefinedError] and subclasses
 *
 * @author Enrico
 */
internal class PredefinedErrorTest {

    private val callableTypeErrorActualValue = Integer.of(1)
    private val aCallableTypeError = TypeError(TypeError.Expected.CALLABLE, callableTypeErrorActualValue)

    @Test
    fun toThrowStructPassesParameterToErrorStructOf() {
        val extraData = Atom.of("extra")
        assertEquals(ErrorUtils.errorStructOf(Atom.of(InstantiationError.name), extraData), InstantiationError.toThrowStruct(extraData))
    }

    @Test
    fun instantiationErrorNameCorrect() {
        assertEquals("instantiation_error", InstantiationError.name)
    }

    @Test
    fun instantiationErrorToThrowStructCorrect() {
        assertEquals(ErrorUtils.errorStructOf(Atom.of(InstantiationError.name)), InstantiationError.toThrowStruct())
    }

    @Test
    fun systemErrorNameCorrect() {
        assertEquals("system_error", SystemError.name)
    }

    @Test
    fun systemErrorToThrowStructCorrect() {
        assertEquals(ErrorUtils.errorStructOf(Atom.of(SystemError.name)), SystemError.toThrowStruct())
    }

    @Test
    fun typeErrorNameCorrect() {
        assertEquals("type_error", aCallableTypeError.name)
    }

    @Test
    fun typeErrorToThrowStructCorrect() {
        assertEquals(
                ErrorUtils.errorStructOf(
                        Struct.of(aCallableTypeError.name, TypeError.Expected.CALLABLE.toAtom(), callableTypeErrorActualValue)
                ),
                aCallableTypeError.toThrowStruct()
        )
    }

    @Test
    fun typeErrorExpectedTypeCorrect() {
        assertEquals(TypeError.Expected.CALLABLE, aCallableTypeError.expectedType)
    }

    @Test
    fun typeErrorActualValueCorrect() {
        assertEquals(callableTypeErrorActualValue, aCallableTypeError.actualValue)
    }

    @Test
    fun typeErrorExpectedToAtomWorksAsExpected() {
        TypeError.Expected.values().forEach {
            assertEquals(Atom.of(it.toString().toLowerCase()), it.toAtom())
        }
    }
}
