package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [FunctionWrapper]
 *
 * @author Enrico
 */
internal class FunctionWrapperTest {

    private val signature = Signature("a", 0)

    private val underTest = object : FunctionWrapper(signature) {
        override val function: PrologFunction<Term> = PrologFunction.ofNullary { throw NotImplementedError() }
    }

    @Test
    fun descriptionPairCorrect() {
        assertEquals(underTest.signature to underTest.function, underTest.descriptionPair)
    }

}
