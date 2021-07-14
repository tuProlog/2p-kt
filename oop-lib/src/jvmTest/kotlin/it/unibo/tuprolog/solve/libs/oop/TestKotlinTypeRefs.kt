package it.unibo.tuprolog.solve.libs.oop

import org.junit.Test
import kotlin.test.assertEquals

class TestKotlinTypeRefs {

    private fun assertPropertyReturns(typeRef: TypeRef, property: String, expected: Any?) {
        val result = typeRef.invoke(property)
        assertEquals(expected, result.asValue()?.value)
    }

    @Test
    fun callStaticPropertiesOfInt() {
        val type = TypeRef.of(Int::class)
        assertPropertyReturns(type, "MAX_VALUE", Int.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Int.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Int.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Int.SIZE_BYTES)
    }

    @Test
    fun callStaticPropertiesOfShort() {
        val type = TypeRef.of(Short::class)
        assertPropertyReturns(type, "MAX_VALUE", Short.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Short.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Short.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Short.SIZE_BYTES)
    }

    @Test
    fun callStaticPropertiesOfLong() {
        val type = TypeRef.of(Long::class)
        assertPropertyReturns(type, "MAX_VALUE", Long.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Long.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Long.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Long.SIZE_BYTES)
    }

    @Test
    fun callStaticPropertiesOfByte() {
        val type = TypeRef.of(Byte::class)
        assertPropertyReturns(type, "MAX_VALUE", Byte.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Byte.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Byte.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Byte.SIZE_BYTES)
    }

    @Test
    fun callStaticPropertiesOfString() {
        val type = TypeRef.of(String::class)
        assertPropertyReturns(type, "CASE_INSENSITIVE_ORDER", String.CASE_INSENSITIVE_ORDER)
    }

    @Test
    fun callStaticPropertiesOfChar() {
        val type = TypeRef.of(Char::class)
        assertPropertyReturns(type, "MAX_VALUE", Char.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Char.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Char.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Char.SIZE_BYTES)
        assertPropertyReturns(type, "MIN_HIGH_SURROGATE", Char.MIN_HIGH_SURROGATE)
        assertPropertyReturns(type, "MIN_LOW_SURROGATE", Char.MIN_LOW_SURROGATE)
        assertPropertyReturns(type, "MIN_SURROGATE", Char.MIN_SURROGATE)
        assertPropertyReturns(type, "MAX_HIGH_SURROGATE", Char.MAX_HIGH_SURROGATE)
        assertPropertyReturns(type, "MAX_LOW_SURROGATE", Char.MAX_LOW_SURROGATE)
        assertPropertyReturns(type, "MAX_SURROGATE", Char.MAX_SURROGATE)
    }

    @Test
    fun callStaticPropertiesOfDouble() {
        val type = TypeRef.of(Double::class)
        assertPropertyReturns(type, "MAX_VALUE", Double.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Double.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Double.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Double.SIZE_BYTES)
        assertPropertyReturns(type, "NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY)
        assertPropertyReturns(type, "NaN", Double.NaN)
        assertPropertyReturns(type, "POSITIVE_INFINITY", Double.POSITIVE_INFINITY)
    }

    @Test
    fun callStaticPropertiesOfFloat() {
        val type = TypeRef.of(Float::class)
        assertPropertyReturns(type, "MAX_VALUE", Float.MAX_VALUE)
        assertPropertyReturns(type, "MIN_VALUE", Float.MIN_VALUE)
        assertPropertyReturns(type, "SIZE_BITS", Float.SIZE_BITS)
        assertPropertyReturns(type, "SIZE_BYTES", Float.SIZE_BYTES)
        assertPropertyReturns(type, "NEGATIVE_INFINITY", Float.NEGATIVE_INFINITY)
        assertPropertyReturns(type, "NaN", Float.NaN)
        assertPropertyReturns(type, "POSITIVE_INFINITY", Float.POSITIVE_INFINITY)
    }
}
