package it.unibo.tuprolog.solve.libs.oop

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

interface OverloadDetector {
    val recordings: List<Pair<Any, KClass<*>>>
    val size: Int

    fun call(x: Any): String

    fun call(x: String): String

    fun call(x: Boolean): String

    fun call(x: Char): String

    fun call(x: Byte): String

    fun call(x: Short): String

    fun call(x: Int): String

    fun call(x: Float): String

    fun call(x: Long): String

    fun call(x: Double): String

    fun call(x: BigInteger): String

    fun call(x: BigDecimal): String

    fun toList(): List<Any>

    fun reset()

    companion object {
        fun create(): OverloadDetector = OverloadDetectorImpl()
    }
}
