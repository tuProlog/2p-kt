package it.unibo.tuprolog.solve.primitive.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
internal object PrimitiveWrapperUtils {
    /** A default primitive result to be used in tests */
    internal val defaultPrimitiveResult = emptySequence<Nothing>()

    /** A test primitive */
    internal val primitive: Primitive = Primitive { defaultPrimitiveResult }

    /** A function to create a Solve.Request with provided [signature] and [argList] */
    internal fun createPrimitiveRequest(
        signature: Signature,
        argList: List<Term>,
    ) = Solve.Request(signature, argList, DummyInstances.executionContext)

    /** Utility function to create a primitive wrapper */
    internal fun createPrimitiveWrapper(
        signature: Signature,
        uncheckedImplementation: Primitive,
    ): PrimitiveWrapper<ExecutionContext> = PrimitiveWrapper.wrap(signature) { uncheckedImplementation.solve(it) }
}
