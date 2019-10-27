package it.unibo.tuprolog.primitive.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.testutils.PrimitiveUtils.createRequest
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
internal object PrimitiveWrapperUtils {

    /** Utility function to create a primitive wrapper */
    internal inline fun createPrimitiveWrapper(signature: Signature, crossinline uncheckedImplementation: Primitive): PrimitiveWrapper<ExecutionContext> =
            object : PrimitiveWrapper<ExecutionContext>(signature) {
                override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
                        uncheckedImplementation(request)
            }

    /** All primitive Wrappers under test */
    internal val primitiveWrappersToSignatures by lazy {
        PrimitiveUtils.primitiveToGoodRequests(::createPrimitiveWrapper).map { it.first }
                .zip(PrimitiveUtils.primitiveSignatures)
    }

    /** All under test requests */
    private val allRequests by lazy {
        (PrimitiveUtils.primitiveGoodRawRequests + PrimitiveUtils.primitiveBadRawRequests).flatten()
                .map { createRequest(it.extractSignature(), it.argsList) }
    }

    /** All ground requests */
    internal val allGroundRequests by lazy {
        allRequests.filter { it.query.isGround }
                .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }

    /** All non-ground requests */
    internal val nonAllGroundRequests by lazy {
        allRequests.filterNot { it.query.isGround }
                .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }

    /** All requests with all numeric arguments */
    internal val allNumericArgsRequests by lazy {
        allRequests.filter { it.arguments.all(Term::isNumber) }
                .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }

    /** All requests with not all numeric arguments */
    internal val notAllNumericArgsRequest by lazy {
        allRequests.filterNot { it.arguments.all(Term::isNumber) }
                .also { assertTrue("Test data empty") { it.isNotEmpty() } }
    }

    /** Utility function to assert over thrown exception */
    internal inline fun <reified E : TuPrologRuntimeException> assertOnError(throwExpression: () -> Unit, assertion: (E) -> Unit) =
            try {
                throwExpression()
                fail("Expected an Exception to be thrown!")
            } catch (error: TuPrologRuntimeException) {
                assertTrue("Thrown error `${error::class}` is not of expected type `${E::class}`") { error is E }
                assertion(error as E)
            }
}
