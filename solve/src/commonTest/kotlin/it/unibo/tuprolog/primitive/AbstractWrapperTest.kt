package it.unibo.tuprolog.primitive

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractWrapper]
 *
 * @author Enrico
 */
internal class AbstractWrapperTest {

    private val signature = Signature("a", 0)

    private val underTest1 = object : AbstractWrapper<Int>(signature) {
        override val wrappedImplementation: Int = 1
    }

    private val underTest2 = object : AbstractWrapper<Int>(signature.name, signature.arity) {
        override val wrappedImplementation: Int = 2
    }

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
