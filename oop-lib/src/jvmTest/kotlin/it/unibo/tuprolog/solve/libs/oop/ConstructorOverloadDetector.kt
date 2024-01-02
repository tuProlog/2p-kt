package it.unibo.tuprolog.solve.libs.oop

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

class ConstructorOverloadDetector {
    val args: Pair<Any, KClass<*>>

    constructor(x: Any) {
        args = x to Any::class
    }

    constructor(x: String) {
        args = x to String::class
    }

    constructor(x: Boolean) {
        args = x to Boolean::class
    }

    constructor(x: Char) {
        args = x to Char::class
    }

    constructor(x: Byte) {
        args = x to Byte::class
    }

    constructor(x: Short) {
        args = x to Short::class
    }

    constructor(x: Int) {
        args = x to Int::class
    }

    constructor(x: Float) {
        args = x to Float::class
    }

    constructor(x: Long) {
        args = x to Long::class
    }

    constructor(x: Double) {
        args = x to Double::class
    }

    constructor(x: BigInteger) {
        args = x to BigInteger::class
    }

    constructor(x: BigDecimal) {
        args = x to BigDecimal::class
    }
}
