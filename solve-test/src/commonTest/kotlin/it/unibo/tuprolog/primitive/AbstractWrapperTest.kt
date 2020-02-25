package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.AbstractWrapper
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractWrapper]
 *
 * @author Enrico
 */
internal class AbstractWrapperTest {

    private val signature = Signature("a", 0)

    private val underTest1 = AbstractWrapper.OfConstant(signature, 1)

    private val underTest2 = AbstractWrapper.OfConstant(signature.name, signature.arity, 2)

    private val allUnderTest = listOf(underTest1, underTest2)

    @Test
    fun signatureCorrect() {
        allUnderTest.forEach {
            assertEquals(signature, it.signature)
        }
    }

    @Test
    fun functorCorrect() {
        allUnderTest.forEach {
            assertEquals(signature.name, it.functor)
        }
    }

    @Test
    fun descriptionPairCorrect() {
        allUnderTest.forEach {
            assertEquals(Pair(signature, it.wrappedImplementation), it.descriptionPair)
        }
    }
}
