package it.unibo.tuprolog.primitive.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.collections.List as KtList

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
internal object PrimitiveWrapperUtils {

    /** Test data in form (Signature, goodRequestList, badRequestList)*/
    private val primitiveSignaturesToGoodAndBadRequests by lazy {
        listOf(
                Triple(Signature("ciao", 0),
                        listOf(Atom.of("ciao")),
                        listOf(
                                Atom.of("ciaO"), Atom.of("cc")
                        )
                ),
                Triple(Signature("another", 1),
                        listOf(
                                Struct.of("another", Var.anonymous()),
                                Struct.of("another", Truth.`true`())
                        ),
                        listOf(
                                Atom.of("another"),
                                Struct.of("another", Var.of("A"), Var.of("B")),
                                Struct.of("other", Integer.of(2))
                        )
                ),
                Triple(Signature("aVarargOne", 1, true),
                        listOf(
                                Struct.of("aVarargOne", Var.of("X")),
                                Struct.of("aVarargOne", Integer.of(1), Real.of(1.5)),
                                Struct.of("aVarargOne", Var.of("X"), Var.of("Y"), Truth.`true`())
                        ),
                        listOf(
                                Atom.of("aVarargOne"),
                                Struct.of("aVararg", Var.anonymous())
                        )
                )
        )
    }

    /** Utility function to create a primitive wrapper */
    private inline fun createPrimitiveWrapper(signature: Signature, crossinline uncheckedImplementation: Primitive): PrimitiveWrapper<ExecutionContext> =
            object : PrimitiveWrapper<ExecutionContext>(signature) {
                override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
                        uncheckedImplementation(request)
            }

    /** All primitives under test associated with good requests; primitives return `emptySequence()` */
    internal val primitivesToCorrectRequests by lazy {
        primitiveSignaturesToGoodAndBadRequests.map { (signature, good, _) ->
            createPrimitiveWrapper(signature) { emptySequence() } to good
        }
    }

    /** All primitives under test associated with bad requests that should be rejected */
    internal val primitivesToBadRequests by lazy {
        primitiveSignaturesToGoodAndBadRequests.map { (signature, _, bad) ->
            createPrimitiveWrapper(signature) { emptySequence() } to bad
        }
    }

    /** All primitive Wrappers under test */
    internal val primitiveWrappersToSignatureMap by lazy {
        primitiveSignaturesToGoodAndBadRequests.map { it.first }.zip(primitivesToCorrectRequests.map { it.first })
    }

    /** A function to create a Solve.Request with provided [signature] and [argList] */
    internal fun createRequest(signature: Signature, argList: KtList<Term>) =
            Solve.Request(signature, argList, DummyInstances.executionContext)

    /** All under test requests */
    private val allRequests by lazy {
        primitiveSignaturesToGoodAndBadRequests
                .flatMap { (_, good, bad) -> good + bad }
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
