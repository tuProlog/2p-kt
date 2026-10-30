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
            TestDatum("atom-a", Atom.of("a"), String::class, "a"),
            TestDatum("integer-one", Integer.ONE, BigInteger::class, BigInteger.ONE),
            TestDatum("real-one", Real.ONE, BigDecimal::class, BigDecimal.ONE),
            TestDatum("true", Truth.TRUE, Boolean::class, true),
            TestDatum("fail", Truth.FAIL, Boolean::class, false),
            TestDatum("false", Truth.FALSE, Boolean::class, false),
            TestDatum("anonymous-var", Var.anonymous(), Any::class, null),
            TestDatum("var-x", Var.of("X"), Any::class, null),
            TestDatum("object-b", A.B(1, "a"), Any::class),
            TestDatum("object-c", A.C(2, "b"), Any::class),
            TestDatum("null", null, Any::class),
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
            TestDatum("atom-a", Atom.of("a"), String::class, "a"),
            TestDatum("atom-a", Atom.of("a"), Char::class, 'a'),
            TestDatum("integer-one", Integer.ONE, BigInteger::class, BigInteger.ONE),
            TestDatum("integer-one", Integer.ONE, BigDecimal::class, BigDecimal.ONE),
            TestDatum("integer-one", Integer.ONE, Int::class, 1),
            TestDatum("integer-one", Integer.ONE, Short::class, 1.toShort()),
            TestDatum("integer-one", Integer.ONE, Long::class, 1.toLong()),
            TestDatum("integer-one", Integer.ONE, Byte::class, 1.toByte()),
            TestDatum("integer-one", Integer.ONE, Double::class, 1.0),
            TestDatum("integer-one", Integer.ONE, Float::class, 1.0f),
            TestDatum("real-one", Real.ONE, BigDecimal::class, BigDecimal.ONE),
            TestDatum("real-one", Real.ONE, Float::class, 1.0f),
            TestDatum("real-one", Real.ONE, Double::class, 1.0),
            TestDatum("true", Truth.TRUE, Boolean::class, true),
            TestDatum("true", Truth.TRUE, String::class, "true"),
            TestDatum("fail", Truth.FAIL, Boolean::class, false),
            TestDatum("fail", Truth.FAIL, String::class, "fail"),
            TestDatum("false", Truth.FALSE, Boolean::class, false),
            TestDatum("false", Truth.FALSE, String::class, "false"),
            TestDatum("anonymous-var", Var.anonymous(), Any::class, null),
            TestDatum("var-x", Var.of("X"), Any::class, null),
            TestDatum("object-b", A.B(1, "a"), Any::class),
            TestDatum("object-c", A.C(2, "b"), Any::class),
            TestDatum("null", null, Any::class),
        )

    private fun Term.product(
        vararg types: KClass<*>,
        datum: (Term, KClass<*>) -> TestDatum,
    ): List<TestDatum> = sequenceOf(this).product(sequenceOf(*types)).map { (t, k) -> datum(t, k) }.toList()

    private fun MutableList<TestDatum>.cornerCase(
        termId: String,
        term: Term,
        vararg types: KClass<*>,
    ) {
        addAll(term.product(*types) { t, k -> TestDatum.failed(termId, t, k) })
    }

    @Suppress("ktlint:standard:multiline-expression-wrapping")
    val cornerCases: List<TestDatum> = buildList {
        cornerCase(
            "var-x",
            Var.of("X"),
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
        )
        cornerCase(
            "anonymous-var",
            Var.anonymous(),
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
        )
        cornerCase(
            "object-b",
            ObjectRef.of(A.B(1, "a")),
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
        )
        cornerCase(
            "object-c",
            ObjectRef.of(A.C(2, "b")),
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
        )
        cornerCase(
            "true",
            Truth.TRUE,
            Char::class,
            BigInteger::class,
            BigDecimal::class,
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Double::class,
            Float::class,
        )
        cornerCase(
            "fail",
            Truth.FAIL,
            Char::class,
            BigInteger::class,
            BigDecimal::class,
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Double::class,
            Float::class,
        )
        cornerCase(
            "false",
            Truth.FALSE,
            Char::class,
            BigInteger::class,
            BigDecimal::class,
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Double::class,
            Float::class,
        )
        cornerCase(
            "atom-a",
            Atom.of("a"),
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
        cornerCase(
            "atom-ab",
            Atom.of("ab"),
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
        cornerCase(
            "integer-above-max-long",
            Integer.of(higherThanMaxLong),
            String::class,
            Char::class,
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-below-min-long",
            Integer.of(lowerThanMinLong),
            String::class,
            Char::class,
            Int::class,
            Short::class,
            Long::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-above-max-int",
            Integer.of(higherThanMaxInt),
            String::class,
            Char::class,
            Int::class,
            Short::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-below-min-int",
            Integer.of(lowerThanMinInt),
            String::class,
            Char::class,
            Int::class,
            Short::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-above-max-short",
            Integer.of(higherThanMaxShort),
            String::class,
            Char::class,
            Short::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-below-min-short",
            Integer.of(lowerThanMinShort),
            String::class,
            Char::class,
            Short::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-above-max-byte",
            Integer.of(higherThanMaxByte),
            String::class,
            Char::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-below-min-byte",
            Integer.of(lowerThanMinByte),
            String::class,
            Char::class,
            Byte::class,
            Boolean::class,
        )
        cornerCase(
            "integer-zero",
            Integer.of(0),
            String::class,
            Char::class,
            Boolean::class,
        )
    }
}
