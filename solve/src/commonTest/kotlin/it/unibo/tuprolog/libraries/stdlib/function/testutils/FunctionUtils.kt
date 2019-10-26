package it.unibo.tuprolog.libraries.stdlib.function.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.libraries.stdlib.function.BinaryMathFunction
import it.unibo.tuprolog.libraries.stdlib.function.UnaryMathFunction
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.assertFailsWith

/**
 * Utils singleton to help testing functions
 *
 * @author Enrico
 */
internal object FunctionUtils {

    /** Utility function to assert that a unaryMathFunction rejects non integer parameters */
    internal fun assertRejectsNonIntegerParameters(unaryMathFunction: UnaryMathFunction){
        assertFailsWith<TypeError> { unaryMathFunction.function(Real.of(2.0), DummyInstances.executionContext) }
    }

    /** Utility function to assert that a binaryMathFunction rejects non integer parameters */
    internal fun assertRejectsNonIntegerParameters(binaryMathFunction: BinaryMathFunction){
        assertFailsWith<TypeError> { binaryMathFunction.function(Integer.of(2), Real.of(2.0), DummyInstances.executionContext) }
        assertFailsWith<TypeError> { binaryMathFunction.function(Real.of(2.0), Integer.of(2), DummyInstances.executionContext) }
        assertFailsWith<TypeError> { binaryMathFunction.function(Real.of(2.0), Real.of(2.0), DummyInstances.executionContext) }
    }

}
