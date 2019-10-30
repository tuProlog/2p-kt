package it.unibo.tuprolog.libraries.stdlib.function.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.stdlib.function.BinaryMathFunction
import it.unibo.tuprolog.libraries.stdlib.function.UnaryMathFunction
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.function.Compute
import it.unibo.tuprolog.primitive.function.FunctionWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.assertFailsWith

/**
 * Utils singleton to help testing functions
 *
 * @author Enrico
 */
internal object FunctionUtils {

    /** Utility function to create a Compute request for functions  */
    private fun createComputeRequest(signature: Signature, vararg argument: Term) =
            Compute.Request(signature, argument.toList(), DummyInstances.executionContext)

    /** Helper function that invokes the function wrapper implementation with provided arguments */
    internal fun FunctionWrapper<ExecutionContext>.computeOf(vararg argument: Term): Term =
            wrappedImplementation(createComputeRequest(signature, *argument)).result

    /** Utility function to assert that a unaryMathFunction rejects non integer parameters */
    internal fun assertRejectsNonIntegerParameters(unaryMathFunction: UnaryMathFunction) {
        assertFailsWith<TypeError> { unaryMathFunction.computeOf(Real.of(2.0)) }
    }

    /** Utility function to assert that a binaryMathFunction rejects non integer parameters */
    internal fun assertRejectsNonIntegerParameters(binaryMathFunction: BinaryMathFunction) {
        assertFailsWith<TypeError> { binaryMathFunction.computeOf(Integer.of(2), Real.of(2.0)) }
        assertFailsWith<TypeError> { binaryMathFunction.computeOf(Real.of(2.0), Integer.of(2)) }
        assertFailsWith<TypeError> { binaryMathFunction.computeOf(Real.of(2.0), Real.of(2.0)) }
    }
}
