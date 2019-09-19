package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [PrimitiveWrapper]
 *
 * @author Enrico
 */
internal class PrimitiveWrapperTest {

    private val signature = Signature("ciao", 0)
    private val underTestWrapper = object : PrimitiveWrapper(signature) {
        override val uncheckedImplementation: Primitive = { emptySequence() }
    }

    @Test
    fun signatureCorrect() {
        assertEquals(signature, underTestWrapper.signature)
    }

    @Test
    fun functorCorrect() {
        assertEquals("ciao", underTestWrapper.functor)
    }

    @Test
    fun primitiveWorksIfCorrectRequestProvided() {
        assertEquals(emptySequence(), underTestWrapper.primitive(DummyInstances.solveRequest.copy(signature)))
    }

    @Test
    fun primitiveComplainsWithWrongRequestSignatureOrArguments() {
        assertFailsWith<IllegalArgumentException> { underTestWrapper.primitive(DummyInstances.solveRequest.copy(signature.copy("hey"))) }
        assertFailsWith<IllegalArgumentException> { underTestWrapper.primitive(DummyInstances.solveRequest.copy(signature.copy(arity = 1), listOf(Truth.fail()))) }
    }

    @Test
    fun descriptionPairCorrect() {
        assertEquals(with(underTestWrapper) { signature to primitive }, underTestWrapper.descriptionPair)
    }
}
