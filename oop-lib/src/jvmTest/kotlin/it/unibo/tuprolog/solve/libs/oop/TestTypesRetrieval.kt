package it.unibo.tuprolog.solve.libs.oop

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestTypesRetrieval {

    private fun assertFactoryRetrievesTypeCorrectly(factory: TypeFactory, type: KClass<*>) {
        assertEquals(type, factory.typeFromName(type.fullName))
        assertEquals(TypeRef.of(type), factory.typeRefFromName(type.fullName))
    }

    private fun assertFactoryRetrievesKotlinTypeFromJavaName(factory: TypeFactory, kType: KClass<*>, jType: Class<*>) {
        assertEquals(kType, factory.typeFromName(jType.name))
        assertEquals(TypeRef.of(kType), factory.typeRefFromName(jType.name))
    }

    private fun assertFactoryRetrievesNoType(factory: TypeFactory, typeName: String) {
        assertNull(factory.typeFromName(typeName))
        assertNull(factory.typeRefFromName(typeName))
    }

    @Test
    fun testTypeFactoryWithCustomTypes() {
        val factory = TypeFactory.default
        assertFactoryRetrievesTypeCorrectly(factory, A::class)
        assertFactoryRetrievesTypeCorrectly(factory, A.B::class)
        assertFactoryRetrievesTypeCorrectly(factory, A.C::class)
        assertFactoryRetrievesTypeCorrectly(factory, Conversions::class)
        assertFactoryRetrievesTypeCorrectly(factory, TestDatum::class)
        assertFactoryRetrievesTypeCorrectly(factory, OverloadDetectorObject::class)
        assertFactoryRetrievesTypeCorrectly(factory, OverloadDetector::class)
        assertFactoryRetrievesTypeCorrectly(factory, OverloadDetectorImpl::class)
    }

    @Test
    fun testTypeFactoryWithWrongCustomTypes() {
        val factory = TypeFactory.default
        assertFactoryRetrievesNoType(factory, A::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, A.B::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, A.C::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Conversions::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, TestDatum::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, OverloadDetectorObject::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, OverloadDetector::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, OverloadDetectorImpl::class.fullName + "1")
    }

    @Test
    fun testTypeFactoryWithKotlinTypes() {
        val factory = TypeFactory.default
        assertFactoryRetrievesTypeCorrectly(factory, Any::class)
        assertFactoryRetrievesTypeCorrectly(factory, String::class)
        assertFactoryRetrievesTypeCorrectly(factory, CharSequence::class)
        assertFactoryRetrievesTypeCorrectly(factory, Throwable::class)
        assertFactoryRetrievesTypeCorrectly(factory, Cloneable::class)
        assertFactoryRetrievesTypeCorrectly(factory, Number::class)
        assertFactoryRetrievesTypeCorrectly(factory, Comparable::class)
        assertFactoryRetrievesTypeCorrectly(factory, Enum::class)
        assertFactoryRetrievesTypeCorrectly(factory, Annotation::class)
        assertFactoryRetrievesTypeCorrectly(factory, Iterable::class)
        assertFactoryRetrievesTypeCorrectly(factory, Iterator::class)
        assertFactoryRetrievesTypeCorrectly(factory, Collection::class)
        assertFactoryRetrievesTypeCorrectly(factory, List::class)
        assertFactoryRetrievesTypeCorrectly(factory, Set::class)
        assertFactoryRetrievesTypeCorrectly(factory, ListIterator::class)
        assertFactoryRetrievesTypeCorrectly(factory, Map::class)
        assertFactoryRetrievesTypeCorrectly(factory, Map.Entry::class)
        assertFactoryRetrievesTypeCorrectly(factory, Boolean::class)
        assertFactoryRetrievesTypeCorrectly(factory, Char::class)
        assertFactoryRetrievesTypeCorrectly(factory, Byte::class)
        assertFactoryRetrievesTypeCorrectly(factory, Short::class)
        assertFactoryRetrievesTypeCorrectly(factory, Int::class)
        assertFactoryRetrievesTypeCorrectly(factory, Float::class)
        assertFactoryRetrievesTypeCorrectly(factory, Long::class)
        assertFactoryRetrievesTypeCorrectly(factory, Double::class)
        assertFactoryRetrievesTypeCorrectly(factory, Nothing::class)
    }

    @Test
    fun testTypeFactoryWithWrongKotlinTypes() {
        val factory = TypeFactory.default
        assertFactoryRetrievesNoType(factory, Any::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, String::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, CharSequence::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Throwable::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Cloneable::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Number::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Comparable::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Enum::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Annotation::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Iterable::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Iterator::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Collection::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, List::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Set::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, ListIterator::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Map::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Map.Entry::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Boolean::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Char::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Byte::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Short::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Int::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Float::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Long::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Double::class.fullName + "1")
        assertFactoryRetrievesNoType(factory, Nothing::class.fullName + "1")
    }

    @Test
    fun testTypeFactoryWithJavaTypes() {
        val factory = TypeFactory.default
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Any::class, java.lang.Object::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, String::class, java.lang.String::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, CharSequence::class, java.lang.CharSequence::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Throwable::class, java.lang.Throwable::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Cloneable::class, java.lang.Cloneable::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Number::class, java.lang.Number::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Comparable::class, java.lang.Comparable::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Enum::class, java.lang.Enum::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Annotation::class, java.lang.annotation.Annotation::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Iterable::class, java.lang.Iterable::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Iterator::class, java.util.Iterator::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Collection::class, java.util.Collection::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, List::class, java.util.List::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Set::class, java.util.Set::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, ListIterator::class, java.util.ListIterator::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Map::class, java.util.Map::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Map.Entry::class, java.util.Map.Entry::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Boolean::class, java.lang.Boolean::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Char::class, java.lang.Character::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Byte::class, java.lang.Byte::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Short::class, java.lang.Short::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Int::class, java.lang.Integer::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Float::class, java.lang.Float::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Long::class, java.lang.Long::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Double::class, java.lang.Double::class.java)
        assertFactoryRetrievesKotlinTypeFromJavaName(factory, Nothing::class, java.lang.Void::class.java)
    }
}
