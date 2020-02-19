package it.unibo.tuprolog.primitive.function.testutils

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.primitive.function.Compute
import it.unibo.tuprolog.primitive.function.FunctionWrapper
import it.unibo.tuprolog.primitive.function.PrologFunction
import it.unibo.tuprolog.primitive.testutils.WrapperUtils
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.test.assertTrue

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
internal object FunctionWrapperUtils {

    /** A default function result to be used in tests */
    internal val defaultFunctionResult = Compute.Response(Truth.`true`())
    /** A test function */
    internal val function: PrologFunction = { defaultFunctionResult }

    /** A function to create a Compute.Request with provided [signature] and [argList] */
    internal fun createFunctionRequest(signature: Signature, argList: List<Term>) =
        Compute.Request(signature, argList, DummyInstances.executionContext)

    /** Utility function to create a function wrapper */
    internal inline fun createFunctionWrapper(
        signature: Signature,
        crossinline uncheckedImplementation: PrologFunction
    ): FunctionWrapper<ExecutionContext> =
        object : FunctionWrapper<ExecutionContext>(signature) {
            override fun uncheckedImplementation(request: Compute.Request<ExecutionContext>): Compute.Response =
                uncheckedImplementation(request)
        }

    /** All under test requests */
    private val allRequests by lazy {
        (WrapperUtils.allMatchingRawStruct + WrapperUtils.allNotMatchingStruct).flatten()
            .map { createFunctionRequest(it.extractSignature(), it.argsList) }
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
}
