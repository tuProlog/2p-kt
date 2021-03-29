package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopException
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.utils.product
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import org.junit.Test
import java.lang.NullPointerException
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class TestRefs {

    private data class TestDatum(val term: Term, val type: KClass<*>, val converted: Any?) {
        val string: String get() = type.name

        constructor(obj: Any?, type: KClass<*>) :
            this(obj?.let { ObjectRef.of(obj) } ?: ObjectRef.NULL, type, obj)

        val exception by lazy { converted as? OopException }

        val isFailed: Boolean
            get() = converted == null || exception != null

        companion object {
            fun failed(term: Term, type: KClass<*>): TestDatum {
                return TestDatum(term, type, TermToObjectConversionException(term, type))
            }
        }
    }

    private val bestCases = listOf(
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

    private val cases = listOf(
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

    private val cornerCases: List<TestDatum> =
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

    private fun testMethodInvocation(
        cases: List<TestDatum>,
        detectorCreator: () -> OverloadDetector = OverloadDetector.Companion::create,
        refCreator: (OverloadDetector) -> Ref = ObjectRef.Companion::of,
        case2Term: (TestDatum) -> Term
    ) {
        val obj = detectorCreator()
        val ref = refCreator(obj)
        for (case in cases) {
            try {
                val result = ref.invoke("call", case2Term(case))
                assertFalse { case.isFailed }
                assertTrue { result is Result.Value }
                assertEquals(case.string, result.asObjectRef()?.`object`)
                assertEquals(Atom.of(case.string), result.toTerm())
            } catch (e: OopRuntimeException) {
                assertTrue { case.term.let { it is Var || it is NullRef } }
            } catch (e: OopException) {
                assertTrue { case.isFailed }
                e.printStackTrace()
                System.err.println(case.converted)
                assertEquals(case.exception!!::class, e::class)
            }
        }
        var result = ref.invoke("size")
        val expectedSize = cases.size - cases.filter { it.isFailed }.count()
        assertEquals(Integer.of(expectedSize), result.toTerm())
        result = ref.invoke("toList")
        val list = result.asObjectRef()?.`object` as List<*>
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! }, list)
        assertEquals(cases.filterNot { it.isFailed }.map { it.converted!! to it.type }, obj.recordings)
    }

    @Test
    fun mostProperOverloadIsSelectedWhenInvokingObjectRefMethod() {
       testMethodInvocation(bestCases) { it.term }
    }

    @Test
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingObjectRefMethod() {
        testMethodInvocation(cases) {
            Struct.of("as", it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun overloadSelectionMayFailWhenInvokingObjectRefMethod() {
        testMethodInvocation(cornerCases) {
            Struct.of("as", it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun mostProperOverloadIsSelectedWhenInvokingTypeRefMethod() {
        OverloadDetectorObject.reset()
        testMethodInvocation(
            bestCases,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) { it.term }
    }

    @Test
    fun overloadCanBeSelectedViaExplicitCastWhenInvokingTypeRefMethod() {
        OverloadDetectorObject.reset()
        testMethodInvocation(
            cases,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) {
            Struct.of("as", it.term, Atom.of(it.type.fullName))
        }
    }

    @Test
    fun overloadSelectionMayFailWhenInvokingTypeRefMethod() {
        OverloadDetectorObject.reset()
        testMethodInvocation(
            cornerCases,
            refCreator = { TypeRef.of(OverloadDetectorObject::class) }
        ) {
            Struct.of("as", it.term, Atom.of(it.type.fullName))
        }
    }

}
