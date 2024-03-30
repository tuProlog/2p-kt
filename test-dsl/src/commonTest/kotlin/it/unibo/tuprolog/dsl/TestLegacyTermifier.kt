package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test

open class TestLegacyTermifier : AbstractTermificatorTest() {
    override fun createTermificator(scope: Scope): Termificator = Termificator.legacy(scope)

    @Test
    fun testTerm() {
        val someTerm = Var.of("X")
        assertTermificationWorks(someTerm, someTerm)
    }

    @Test
    fun testBooleans() {
        assertTermificationWorks(true, Scope::truthOf)
        assertTermificationWorks(false, Scope::truthOf)
    }

    @Test
    fun testStrings() {
        assertTermificationWorks("Xs", Scope::varOf)
        assertTermificationWorks("_", Scope::varOf)
        assertTermificationWorks("_X", Scope::varOf)
        assertTermificationWorks("_x", Scope::varOf)
        assertTermificationWorks("xs", Scope::atomOf)
    }

    @Test
    fun testChars() {
        assertTermificationWorks('X', Scope::varOf)
        assertTermificationWorks('_', Scope::varOf)
        assertTermificationWorks('x', Scope::atomOf)
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
        assertTermificationWorks(3.1, { realOf(it.toString()) })
        assertTermificationWorks(3.1f, { realOf(it.toString()) })
        assertTermificationWorks(1.0, PlatformSpecificValues.ONE_POINT_ZERO)
        assertTermificationWorks(-3, PlatformSpecificValues.MINUS_THREE)
    }

    @Test
    fun testArrays() {
        assertTermificationWorks(array, { array.map { intOf(it) }.let(this::logicListOf) })
    }

    @Test
    fun testLists() {
        assertTermificationWorks(list, { list.map { intOf(it) }.let(this::logicListOf) })
    }

    @Test
    open fun testSets() {
        assertTermificationWorks(set, { set.map { intOf(it) }.let(this::logicListOf) })
    }

    @Test
    fun testSequences() {
        assertTermificationWorks(sequence, { sequence.map { intOf(it) }.let(this::logicListOf) })
    }

    @Test
    open fun testMaps() {
        assertTermificationFails(map)
    }

    @Test
    open fun testPairs() {
        assertTermificationFails(pair)
    }

    @Test
    open fun testTriples() {
        assertTermificationFails(triple)
    }

    @Test
    open fun testKeyValues() {
        assertTermificationFails(keyValue)
    }

    @Test
    open fun testNonConvertibleObject() {
        assertTermificationFails(unconvertible)
    }
}
