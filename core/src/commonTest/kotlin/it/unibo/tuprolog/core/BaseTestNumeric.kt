package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
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
                it.matches(Integral.INTEGRAL_REGEX_PATTERN) -> Integral.of(it)
                it.matches(Real.REAL_REGEX_PATTERN) -> Real.of(it)
                else -> throw IllegalArgumentException(it)
            }
        }.toList()

        for (xy in creation.zip(numbersUnderTest)) {
            assertEquals(xy.second, xy.first, "${xy.second} == ${xy.first} is failing")
        }

    }

}