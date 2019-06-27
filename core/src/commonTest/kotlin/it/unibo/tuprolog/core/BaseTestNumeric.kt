package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.*

import kotlin.collections.List as KtList

abstract class BaseTestNumeric {

    companion object {
        fun assertEquals(x: BigDecimal, y: BigDecimal, m: String? = "Failed: $x == $y") {
            if (m === null) {
                assertEquals(0, x.compareTo(y))
                assertEquals(0, y.compareTo(x))
            } else {
                assertEquals(0, x.compareTo(y), m)
                assertEquals(0, y.compareTo(x), m)
            }
        }

        fun assertEquals(x: BigInteger, y: BigInteger, m: String? = "Failed: $x == $y") {
            if (m === null) {
                assertEquals(0, x.compareTo(y))
                assertEquals(0, y.compareTo(x))
            } else {
                assertEquals(0, x.compareTo(y), m)
                assertEquals(0, y.compareTo(x), m)
            }
        }

        fun assertReprEquals(repr: Any, obj: Any, m: String? = "Failed: $obj.toString() == $repr") {
            if (m === null) {
                assertEquals(repr.toString(), obj.toString())
            } else {
                assertEquals(repr.toString(), obj.toString(), m)
            }
        }
    }

    private val intPattern = """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()
    private val i = """([0-9]+)"""
    private val d = """(\.[0-9]+)"""
    private val e = """([eE][+\-]?[0-9]+)"""
    private val realPattern = "^[+\\-]?(($i$d$e?)|($i$e)|($d$e?))$".toRegex()

    abstract val numbersUnderTestAsStrings: KtList<String>
    abstract val numbersUnderTestValues: KtList<Any>

    val numbersUnderTest: KtList<Numeric> by lazy {
        numbersUnderTestAsStrings.map { Numeric.of(it) }.toList()
    }

//    private fun getRadix(str: String): Int {
//        return when {
//            str.contains("B", ignoreCase = true) -> 2
//            str.contains("O", ignoreCase = true) -> 8
//            str.contains("X", ignoreCase = true) -> 16
//            else -> 10
//        }
//    }

    @Test
    fun creationAsNumbers1() {
        val creation = numbersUnderTestAsStrings.map {
            when {
                it.matches(intPattern) -> Integral.of(it)
                it.matches(realPattern) -> Real.of(it)
                else -> throw IllegalArgumentException(it)
            }
        }.toList()

        for (xy in creation.zip(numbersUnderTest)) {
            assertEquals(xy.second, xy.first, "${xy.second} == ${xy.first} is failing")
        }

    }

    @Test
    fun creationAsNumbers2() {

        for (xy in numbersUnderTest.zip(numbersUnderTestValues)) {
            when (xy.first) {
                is Integral -> {
                    assertEquals(xy.first.intValue, xy.second as BigInteger)
                }
                is Real -> {
                    assertEquals(xy.first.decimalValue, xy.second as BigDecimal)
                }
                else -> throw IllegalArgumentException(xy.first.toString())
            }
        }
    }

    @Test
    fun cloneAsAtom() {

        numbersUnderTest.forEach {
            assertEquals(it, it.freshCopy())
            assertSame(it, it.freshCopy())
            assertTrue(it.structurallyEquals(it.freshCopy()))
            assertTrue(it.structurallyEquals(it.freshCopy()))
        }
    }

    @Test
    fun typeTesting() {
        numbersUnderTestAsStrings.forEach {str ->

            val it = Numeric.of(str)

            when {
                str.matches(intPattern) -> assertTrue(it is Integral)
                str.matches(realPattern) -> assertTrue(it is Real)
                else -> throw IllegalArgumentException()
            }
            assertTrue(it is Numeric)
            assertTrue(it is Term)

            when {
                str.matches(intPattern) -> assertTrue(it.isInt)
                str.matches(realPattern) -> assertTrue(it.isReal)
                else -> throw IllegalArgumentException()
            }
            assertTrue(it.isNumber)

            when {
                str.matches(intPattern) -> assertFalse(it is Real)
                str.matches(realPattern) -> assertFalse(it is Integral)
                else -> throw IllegalArgumentException()
            }
            assertFalse(it is Struct)
            assertFalse(it is Clause)
            assertFalse(it is Var)
            assertFalse(it is Couple)

            when {
                str.matches(intPattern) -> assertFalse(it.isReal)
                str.matches(realPattern) -> assertFalse(it.isInt)
                else -> throw IllegalArgumentException()
            }
            assertFalse(it.isStruct)
            assertFalse(it.isAtom)
            assertFalse(it.isClause)
            assertFalse(it.isDirective)
            assertFalse(it.isFact)
            assertFalse(it.isRule)
            assertFalse(it.isCouple)
            assertFalse(it.isVariable)
            assertFalse(it.isVariable)
            assertFalse(it.isBound)
        }
    }

    @Test
    fun value() {
        numbersUnderTest.zip(numbersUnderTestValues).forEach {
            when(it.first){
                is Integral -> with(it.first.castTo<Integral>()) {
                    assertEquals(0, value.compareTo(it.second as BigInteger), "$value == ${it.second} is failing")
                    assertEquals(0, this.compareTo(Integral.of(it.second as BigInteger)), "$this == ${Integral.of(it.second as BigInteger)} is failing")
                    assertEquals(0, value.compareTo(intValue), "$value == $intValue is failing")
                }
                is Real -> with(it.first.castTo<Real>()) {
                    assertEquals(0, value.compareTo(it.second as BigDecimal), "$value == ${it.second} is failing")
                    assertEquals(0, this.compareTo(Real.of(it.second as BigDecimal)), "$this == ${Real.of(it.second as BigDecimal)} is failing")
                    assertEquals(0, value.compareTo(decimalValue), "$value == $decimalValue is failing")
                }
                else -> throw IllegalStateException()
            }
        }
    }

    @Test
    fun toStringRepresentation() {
        numbersUnderTest.forEach {
            when (it) {
                is Integral -> assertTrue(it.toString() matches intPattern, "$it does not matched $intPattern")
                is Real -> assertTrue(it.toString() matches realPattern, "$it does not matched $realPattern")
                else -> throw IllegalArgumentException()
            }
        }

    }

    @Test
    fun comparisons() {
        numbersUnderTest.zip(numbersUnderTestValues).forEach {
            when {
                it.first is Integral -> {
                    val greater = Integral.of((it.second as BigInteger) + BigInteger.ONE)
                    val lower = Integral.of((it.second as BigInteger) - BigInteger.ONE)

                    with(it.first.castTo<Integral>()) {
                        assertTrue(this < greater, "$this < $greater is failing")
                        assertTrue(this > lower, "$this > $lower is failing")
                    }
                }
                it.first is Real -> {
                    val greater = Real.of((it.second as BigDecimal) + BigDecimal.ONE)
                    val lower = Real.of((it.second as BigDecimal) - BigDecimal.ONE)

                    with(it.first.castTo<Real>()) {
                        assertTrue(this < greater, "$this < $greater is failing")
                        assertTrue(this > lower, "$this > $lower is failing")
                    }
                }
                else -> throw IllegalStateException()
            }
        }
    }
}