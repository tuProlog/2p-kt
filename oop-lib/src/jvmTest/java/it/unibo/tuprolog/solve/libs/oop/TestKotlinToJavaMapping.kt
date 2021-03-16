package it.unibo.tuprolog.solve.libs.oop

import org.junit.Test
import kotlin.test.assertEquals

class TestKotlinToJavaMapping {
    @Test
    fun testContentOfMapping() {
        assertEquals(Any::class, KotlinToJavaTypeMap["kotlin.Any"])
        assertEquals(String::class, KotlinToJavaTypeMap["kotlin.String"])
        assertEquals(CharSequence::class, KotlinToJavaTypeMap["kotlin.CharSequence"])
        assertEquals(Throwable::class, KotlinToJavaTypeMap["kotlin.Throwable"])
        assertEquals(Cloneable::class, KotlinToJavaTypeMap["kotlin.Cloneable"])
        assertEquals(Number::class, KotlinToJavaTypeMap["kotlin.Number"])
        assertEquals(Comparable::class, KotlinToJavaTypeMap["kotlin.Comparable"])
        assertEquals(Enum::class, KotlinToJavaTypeMap["kotlin.Enum"])
        assertEquals(Annotation::class, KotlinToJavaTypeMap["kotlin.Annotation"])
        assertEquals(Iterable::class, KotlinToJavaTypeMap["kotlin.collections.Iterable"])
        assertEquals(Iterator::class, KotlinToJavaTypeMap["kotlin.collections.Iterator"])
        assertEquals(Collection::class, KotlinToJavaTypeMap["kotlin.collections.Collection"])
        assertEquals(List::class, KotlinToJavaTypeMap["kotlin.collections.List"])
        assertEquals(Set::class, KotlinToJavaTypeMap["kotlin.collections.Set"])
        assertEquals(ListIterator::class, KotlinToJavaTypeMap["kotlin.collections.ListIterator"])
        assertEquals(Map::class, KotlinToJavaTypeMap["kotlin.collections.Map"])
        assertEquals(Map.Entry::class, KotlinToJavaTypeMap["kotlin.collections.Map.Entry"])
        assertEquals(Boolean::class, KotlinToJavaTypeMap["kotlin.Boolean"])
        assertEquals(Char::class, KotlinToJavaTypeMap["kotlin.Char"])
        assertEquals(Byte::class, KotlinToJavaTypeMap["kotlin.Byte"])
        assertEquals(Short::class, KotlinToJavaTypeMap["kotlin.Short"])
        assertEquals(Int::class, KotlinToJavaTypeMap["kotlin.Int"])
        assertEquals(Float::class, KotlinToJavaTypeMap["kotlin.Float"])
        assertEquals(Long::class, KotlinToJavaTypeMap["kotlin.Long"])
        assertEquals(Double::class, KotlinToJavaTypeMap["kotlin.Double"])
    }
}
