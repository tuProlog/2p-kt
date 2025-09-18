package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractWrapper]
 *
 * @author Enrico
 */
internal class AbstractWrapperTest {
    private val signature = Signature("a", 0)

    private val underTest1 = WrapperOfConstant(signature, 1)
    private val underTest2 = WrapperOfConstant(signature.name, signature.arity, 2)

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
            assertEquals(Pair(signature, it.implementation), it.descriptionPair)
        }
    }

    private companion object {
        /** A testing class for AbstractWrapper functionality */
        private class WrapperOfConstant<out T>(
            signature: Signature,
            value: T,
        ) : AbstractWrapper<T>(signature) {
            // this class was added since Kotlin/JS won't pass tests using "object literals"
            // maybe in future releases of Kotlin the problem will be solved

            constructor(name: String, arity: Int, vararg: Boolean, value: T) :
                this(Signature(name, arity, vararg), value)

            constructor(name: String, arity: Int, value: T) :
                this(Signature(name, arity, false), value)

            override val implementation: T = value
        }
    }
}
