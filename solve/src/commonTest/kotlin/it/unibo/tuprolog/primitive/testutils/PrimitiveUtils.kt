package it.unibo.tuprolog.primitive.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.toSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [Primitive] and factory method
 *
 * @author Enrico
 */
internal object PrimitiveUtils {

    /** Test data in the form of (Signature, goodRequestList, badRequestList)*/
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

    /** All primitive signatures under test */
    internal val primitiveSignatures by lazy { primitiveSignaturesToGoodAndBadRequests.map { it.first } }
    /** All under test primitive good requests */
    internal val primitiveGoodRawRequests by lazy { primitiveSignaturesToGoodAndBadRequests.map { it.second } }
    /** All under test primitive bad requests */
    internal val primitiveBadRawRequests by lazy { primitiveSignaturesToGoodAndBadRequests.map { it.third } }

    /** A function to create a Solve.Request with provided [signature] and [argList] */
    internal fun createRequest(signature: Signature, argList: KtList<Term>) =
            Solve.Request(signature, argList, DummyInstances.executionContext)

    /** All primitives under test associated with good requests; primitives return `emptySequence()` */
    internal inline fun <WrappedType> primitiveToGoodRequests(primitiveWrapperCreator: (Signature, Primitive) -> WrappedType) =
            primitiveSignaturesToGoodAndBadRequests.map { (signature, good, _) ->
                primitiveWrapperCreator(signature) { emptySequence() } to good.map { createRequest(it.toSignature(), it.argsList) }
            }

    /** All primitives under test associated with bad requests that should be rejected */
    internal inline fun <WrappedType> primitiveToBadRequests(primitiveWrapperCreator: (Signature, Primitive) -> WrappedType) =
            primitiveSignaturesToGoodAndBadRequests.map { (signature, _, bad) ->
                primitiveWrapperCreator(signature) { emptySequence() } to bad.map { createRequest(it.toSignature(), it.argsList) }
            }
}
