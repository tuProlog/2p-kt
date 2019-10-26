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

    private val underTest1 = object : AbstractWrapper<Nothing>(signature) {
        override val descriptionPair: Nothing by lazy { throw NotImplementedError() }
    }

    private val underTest2 = object : AbstractWrapper<Nothing>(signature.name, signature.arity) {
        override val descriptionPair: Nothing by lazy { throw NotImplementedError() }
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
}
