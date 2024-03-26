package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test

open class TestDefaultTermifier : AbstractTermificatorTest() {
    override fun createTermificator(scope: Scope): Termificator = Termificator.default(scope)

    @Test
    fun testBooleans() {
        assertTermificationWorks(true, Scope::truthOf)
        assertTermificationWorks(false, Scope::truthOf)
    }

    @Test
    fun testStrings() {
        assertTermificationWorks("X", Scope::varOf)
        assertTermificationWorks("_", Scope::varOf)
        assertTermificationWorks("x", Scope::atomOf)
    }

    @Test
    fun testIntegers() {
        assertTermificationWorks(1, Scope::intOf)
        assertTermificationWorks(BigInteger.ONE, Scope::intOf)
        assertTermificationWorks(1L, Scope::intOf)
        assertTermificationWorks(1.toShort(), Scope::intOf)
        assertTermificationWorks(1.toByte(), Scope::intOf)
    }

    @Test
    fun testFloats() {
        assertTermificationWorks(1.5, Scope::realOf)
        assertTermificationWorks(BigDecimal.ONE + BigDecimal.ONE_HALF, Scope::realOf)
        assertTermificationWorks(1.5f, Scope::realOf)
    }

    @Test
    fun testArrays() {
        val input = arrayOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::listOf) })
    }

    @Test
    fun testLists() {
        val input = listOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::listOf) })
    }

    @Test
    open fun testSets() {
        val input = setOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::listOf) })
    }

    @Test
    fun testSequences() {
        val input = sequenceOf(1, 2)
        assertTermificationWorks(input, { input.map { intOf(it) }.let(::listOf) })
    }
}
