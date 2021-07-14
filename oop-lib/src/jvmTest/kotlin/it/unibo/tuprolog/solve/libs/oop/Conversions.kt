package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.product
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.reflect.KClass

internal object Conversions {
    val bestCases = listOf(
        TestDatum(Atom.of("a"), String::class, "a"),
        TestDatum(Integer.ONE, BigInteger::class, BigInteger.ONE),
        TestDatum(Real.ONE, BigDecimal::class, BigDecimal.ONE),
        TestDatum(Truth.TRUE, Boolean::class, true),
        TestDatum(Truth.FAIL, Boolean::class, false),
        TestDatum(Truth.FALSE, Boolean::class, false),
        TestDatum(Var.anonymous(), Any::class, null),
        TestDatum(Var.of("X"), Any::class, null),
        TestDatum(A.B(1, "a"), Any::class),
        TestDatum(A.C(2, "b"), Any::class),
        TestDatum(null, Any::class),
    )

    private val higherThanMaxLong = BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE
    private val lowerThanMinLong = BigInteger.of(Long.MIN_VALUE) - BigInteger.ONE

    private val higherThanMaxInt = BigInteger.of(Int.MAX_VALUE) + BigInteger.ONE
    private val lowerThanMinInt = BigInteger.of(Int.MIN_VALUE) - BigInteger.ONE

    private val higherThanMaxShort = BigInteger.of(Short.MAX_VALUE.toInt()) + BigInteger.ONE
    private val lowerThanMinShort = BigInteger.of(Short.MIN_VALUE.toInt()) - BigInteger.ONE

    private val higherThanMaxByte = BigInteger.of(Byte.MAX_VALUE.toInt()) + BigInteger.ONE
    private val lowerThanMinByte = BigInteger.of(Byte.MIN_VALUE.toInt()) - BigInteger.ONE

    val explicitCases = listOf(
        TestDatum(Atom.of("a"), String::class, "a"),
        TestDatum(Atom.of("a"), Char::class, 'a'),
        TestDatum(Integer.ONE, BigInteger::class, BigInteger.ONE),
        TestDatum(Integer.ONE, BigDecimal::class, BigDecimal.ONE),
        TestDatum(Integer.ONE, Int::class, 1),
        TestDatum(Integer.ONE, Short::class, 1.toShort()),
        TestDatum(Integer.ONE, Long::class, 1.toLong()),
        TestDatum(Integer.ONE, Byte::class, 1.toByte()),
        TestDatum(Integer.ONE, Double::class, 1.0),
        TestDatum(Integer.ONE, Float::class, 1.0f),
        TestDatum(Real.ONE, BigDecimal::class, BigDecimal.ONE),
        TestDatum(Real.ONE, Float::class, 1.0f),
        TestDatum(Real.ONE, Double::class, 1.0),
        TestDatum(Truth.TRUE, Boolean::class, true),
        TestDatum(Truth.TRUE, String::class, "true"),
        TestDatum(Truth.FAIL, Boolean::class, false),
        TestDatum(Truth.FAIL, String::class, "fail"),
        TestDatum(Truth.FALSE, Boolean::class, false),
        TestDatum(Truth.FALSE, String::class, "false"),
        TestDatum(Var.anonymous(), Any::class, null),
        TestDatum(Var.of("X"), Any::class, null),
        TestDatum(A.B(1, "a"), Any::class),
        TestDatum(A.C(2, "b"), Any::class),
        TestDatum(null, Any::class),
    )

    private fun Term.product(vararg types: KClass<*>, datum: (Term, KClass<*>) -> TestDatum): List<TestDatum> =
        sequenceOf(this).product(sequenceOf(*types)).map { (t, k) -> datum(t, k) }.toList()

    val cornerCases: List<TestDatum> =
        Var.of("X").product(String::class, Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + Var.anonymous().product(String::class, Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + ObjectRef.of(A.B(1, "a")).product(String::class, Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + ObjectRef.of(A.C(2, "b")).product(String::class, Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + Truth.TRUE.product(Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + Truth.FAIL.product(Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + Truth.FALSE.product(Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class) { t, k ->
            TestDatum.failed(t, k)
        } + Atom.of("a").product(BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Atom.of("ab").product(Char::class, BigInteger::class, BigDecimal::class, Int::class, Short::class, Long::class, Byte::class, Double::class, Float::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(higherThanMaxLong).product(String::class, Char::class, Int::class, Short::class, Long::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(lowerThanMinLong).product(String::class, Char::class, Int::class, Short::class, Long::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(higherThanMaxInt).product(String::class, Char::class, Int::class, Short::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(lowerThanMinInt).product(String::class, Char::class, Int::class, Short::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(higherThanMaxShort).product(String::class, Char::class, Short::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(lowerThanMinShort).product(String::class, Char::class, Short::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(higherThanMaxByte).product(String::class, Char::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(lowerThanMinByte).product(String::class, Char::class, Byte::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        } + Integer.of(0).product(String::class, Char::class, Boolean::class) { t, k ->
            TestDatum.failed(t, k)
        }
}
