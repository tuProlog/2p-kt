package it.unibo.tuprolog.solve.libs.oop

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

class OverloadDetectorImpl : OverloadDetector {
    private val _recordings: MutableList<Pair<Any, KClass<*>>> = mutableListOf()

    override val recordings: List<Pair<Any, KClass<*>>>
        get() = _recordings

    override fun call(x: Any): String {
        _recordings.add(x to Any::class)
        return Any::class.name
    }

    override fun call(x: String): String {
        _recordings.add(x to String::class)
        return String::class.name
    }

    override fun call(x: Boolean): String {
        _recordings.add(x to Boolean::class)
        return Boolean::class.name
    }

    override fun call(x: Char): String {
        _recordings.add(x to Char::class)
        return Char::class.name
    }

    override fun call(x: Byte): String {
        _recordings.add(x to Byte::class)
        return Byte::class.name
    }

    override fun call(x: Short): String {
        _recordings.add(x to Short::class)
        return Short::class.name
    }

    override fun call(x: Int): String {
        _recordings.add(x to Int::class)
        return Int::class.name
    }

    override fun call(x: Float): String {
        _recordings.add(x to Float::class)
        return Float::class.name
    }

    override fun call(x: Long): String {
        _recordings.add(x to Long::class)
        return Long::class.name
    }

    override fun call(x: Double): String {
        _recordings.add(x to Double::class)
        return Double::class.name
    }

    override fun call(x: BigInteger): String {
        _recordings.add(x to BigInteger::class)
        return BigInteger::class.name
    }

    override fun call(x: BigDecimal): String {
        _recordings.add(x to BigDecimal::class)
        return BigDecimal::class.name
    }

    override fun toList(): List<Any> = _recordings.map { it.first }

    override fun reset() = _recordings.clear()

    override val size: Int
        get() = _recordings.size
}
