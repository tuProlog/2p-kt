package it.unibo.tuprolog.solve.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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

        val cases =
            listOf(
                WrapperCase("signature-constructor") { WrapperOfConstant(signature, 1) },
                WrapperCase("name-and-arity-constructor") { WrapperOfConstant(signature.name, signature.arity, 2) },
            )

        withTests(nameFn = { "signature-correct/${it.id}" }, cases) { case ->
            assertEquals(signature, case.wrapper().signature)
        }

        withTests(nameFn = { "functor-correct/${it.id}" }, cases) { case ->
            assertEquals(signature.name, case.wrapper().functor)
        }

        withTests(nameFn = { "description-pair-correct/${it.id}" }, cases) { case ->
            val wrapper = case.wrapper()
            assertEquals(Pair(signature, wrapper.implementation), wrapper.descriptionPair)
        }
    })

private class WrapperCase(
    val id: String,
    val wrapper: () -> WrapperOfConstant<Int>,
)

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
