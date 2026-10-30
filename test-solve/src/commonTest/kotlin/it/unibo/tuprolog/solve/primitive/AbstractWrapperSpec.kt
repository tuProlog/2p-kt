package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.solve.Signature
import kotlin.test.assertEquals

/**
 * Test class for [AbstractWrapper]
 *
 * @author Enrico
 */
class AbstractWrapperSpec :
    FunSpec({
        val signature = Signature("a", 0)

        val underTest1 = WrapperOfConstant(signature, 1)

        val underTest2 = WrapperOfConstant(signature.name, signature.arity, 2)

        val allUnderTest = listOf(underTest1, underTest2)

        test("signature-correct") {
            allUnderTest.forEach {
                assertEquals(signature, it.signature)
            }
        }

        test("functor-correct") {
            allUnderTest.forEach {
                assertEquals(signature.name, it.functor)
            }
        }

        test("description-pair-correct") {
            allUnderTest.forEach {
                assertEquals(Pair(signature, it.implementation), it.descriptionPair)
            }
        }
    })

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
