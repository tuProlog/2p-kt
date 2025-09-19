package it.unibo.tuprolog.solve.primitive.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import kotlin.test.assertTrue

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

    /** All under test requests */
    private val allRequests by lazy {
        (WrapperUtils.allMatchingRawStruct + WrapperUtils.allNotMatchingStruct)
            .flatten()
            .map { createPrimitiveRequest(it.extractSignature(), it.args) }
    }

    /** All ground requests */
    internal val allGroundRequests by lazy {
        allRequests
            .filter { it.query.isGround }
            .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }

    /** All non-ground requests */
    internal val nonAllGroundRequests by lazy {
        allRequests
            .filterNot { it.query.isGround }
            .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }
}
