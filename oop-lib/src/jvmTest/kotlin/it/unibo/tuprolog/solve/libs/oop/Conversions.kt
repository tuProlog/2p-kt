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
    val bestCases =
        listOf(
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

    val explicitCases =
        listOf(
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

    private fun Term.product(
        vararg types: KClass<*>,
        datum: (Term, KClass<*>) -> TestDatum,
    ): List<TestDatum> = sequenceOf(this).product(sequenceOf(*types)).map { (t, k) -> datum(t, k) }.toList()

    private inline operator fun <reified T> Array<T>.minus(item: T): Array<T> = this.filterNot { it == item }.toTypedArray()

    val cornerCases: List<TestDatum> =
        buildList {
            val literalTypes =
                arrayOf(
                    String::class,
                    Char::class,
                    BigInteger::class,
                    BigDecimal::class,
                    Int::class,
                    Short::class,
                    Long::class,
                    Byte::class,
                    Double::class,
                    Float::class,
                    Boolean::class,
                )
            val literalTypesExceptBoolean = literalTypes - Boolean::class
            Var.of("X").product(*literalTypesExceptBoolean, datum = TestDatum::failed).let(::addAll)
            Var.anonymous().product(*literalTypesExceptBoolean, datum = TestDatum::failed).let(::addAll)
            ObjectRef.of(A.B(1, "a")).product(*literalTypesExceptBoolean, datum = TestDatum::failed).let(::addAll)
            ObjectRef.of(A.C(2, "b")).product(*literalTypesExceptBoolean, datum = TestDatum::failed).let(::addAll)
            val literalTypesExceptBooleanAndString = literalTypesExceptBoolean - String::class
            Truth.TRUE.product(*literalTypesExceptBooleanAndString, datum = TestDatum::failed).let(::addAll)
            Truth.FAIL.product(*literalTypesExceptBooleanAndString, datum = TestDatum::failed).let(::addAll)
            Truth.FALSE.product(*literalTypesExceptBooleanAndString, datum = TestDatum::failed).let(::addAll)
            val numericLiteralTypes = literalTypes - String::class - Char::class
            Atom.of("a").product(*numericLiteralTypes, datum = TestDatum::failed).let(::addAll)
            val numericLiteralTypesAndChar = literalTypes - String::class
            Atom.of("ab").product(*numericLiteralTypesAndChar, datum = TestDatum::failed).let(::addAll)
            val nonFloatingLiteralTypes = literalTypes - Double::class - Float::class
            Integer.of(higherThanMaxLong).product(*nonFloatingLiteralTypes, datum = TestDatum::failed).let(::addAll)
            Integer.of(lowerThanMinLong).product(*nonFloatingLiteralTypes, datum = TestDatum::failed).let(::addAll)
            val nonFloatingLiteralTypesMinusLong = nonFloatingLiteralTypes - Long::class
            Integer.of(higherThanMaxInt).product(*nonFloatingLiteralTypesMinusLong, datum = TestDatum::failed).let(::addAll)
            Integer.of(lowerThanMinInt).product(*nonFloatingLiteralTypesMinusLong, datum = TestDatum::failed).let(::addAll)
            val shortNumbersAndAlphanumeric = nonFloatingLiteralTypesMinusLong - Int::class
            Integer.of(higherThanMaxShort).product(*shortNumbersAndAlphanumeric, datum = TestDatum::failed).let(::addAll)
            Integer.of(lowerThanMinShort).product(*shortNumbersAndAlphanumeric, datum = TestDatum::failed).let(::addAll)
            val byteBoolsAndAlphanumeric = shortNumbersAndAlphanumeric - Short::class
            Integer.of(higherThanMaxByte).product(*byteBoolsAndAlphanumeric, datum = TestDatum::failed).let(::addAll)
            Integer.of(lowerThanMinByte).product(*byteBoolsAndAlphanumeric, datum = TestDatum::failed).let(::addAll)
            val boolsAndAlphanumeric = byteBoolsAndAlphanumeric - Byte::class
            Integer.of(0).product(*boolsAndAlphanumeric, datum = TestDatum::failed).let(::addAll)
        }
}
