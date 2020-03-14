package it.unibo.tuprolog.primitive.testutils

import it.unibo.tuprolog.AbstractWrapper
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import kotlin.collections.List as KtList

/**
 * Utils singleton to help testing [AbstractWrapper] implementations
 *
 * @author Enrico
 */
internal object WrapperUtils {

    /** Test data in the form of (Signature, matchingList, notMatchingList)*/
    private val signaturesToMatchingAndNotMatchingStruct by lazy {
        listOf(
            Triple(
                Signature("ciao", 0),
                listOf(Atom.of("ciao")),
                listOf(
                    Atom.of("ciaO"), Atom.of("cc")
                )
            ),
            Triple(
                Signature("another", 1),
                listOf(
                    Struct.of("another", Var.anonymous()),
                    Struct.of("another", Truth.ofTrue())
                ),
                listOf(
                    Atom.of("another"),
                    Struct.of("another", Var.of("A"), Var.of("B")),
                    Struct.of("other", Integer.of(2))
                )
            ),
            Triple(
                Signature("aVarargOne", 1, true),
                listOf(
                    Struct.of("aVarargOne", Var.of("X")),
                    Struct.of("aVarargOne", Integer.of(1), Real.of(1.5)),
                    Struct.of("aVarargOne", Var.of("X"), Var.of("Y"), Truth.ofTrue())
                ),
                listOf(
                    Atom.of("aVarargOne"),
                    Struct.of("aVararg", Var.anonymous())
                )
            )
        )
    }

    /** All signatures under test */
    internal val allSignatures by lazy { signaturesToMatchingAndNotMatchingStruct.map { it.first } }
    /** All [Struct]s matching signatures */
    internal val allMatchingRawStruct by lazy { signaturesToMatchingAndNotMatchingStruct.map { it.second } }
    /** All [Struct]s not matching signatures */
    internal val allNotMatchingStruct by lazy { signaturesToMatchingAndNotMatchingStruct.map { it.third } }

    /** Creates a map from under test Wrappers to matching signature requests */
    internal inline fun <Request, WrappedType, WrappingType> wrapperToMatchingSignatureRequest(
        wrapperCreator: (Signature, WrappedType) -> WrappingType,
        wrapped: WrappedType,
        requestCreator: (Signature, KtList<Term>) -> Request
    ) = signaturesToMatchingAndNotMatchingStruct.map { (signature, good, _) ->
        wrapperCreator(signature, wrapped) to good.map { requestCreator(it.extractSignature(), it.argsList) }
    }

    /** Creates a map from under test Wrappers to not matching signature requests which therefore should be rejected */
    internal inline fun <Request, WrappedType, WrappingType> wrapperToNotMatchingSignatureRequest(
        wrapperCreator: (Signature, WrappedType) -> WrappingType,
        wrapped: WrappedType,
        requestCreator: (Signature, KtList<Term>) -> Request
    ) = signaturesToMatchingAndNotMatchingStruct.map { (signature, _, bad) ->
        wrapperCreator(signature, wrapped) to bad.map { requestCreator(it.extractSignature(), it.argsList) }
    }
}
