package it.unibo.tuprolog.solve.libs.oop

import java.util.TreeMap
import kotlin.reflect.KClass

private val map =
    TreeMap<String, KClass<*>>().also { tree ->
        listOf(
            Any::class,
            String::class,
            CharSequence::class,
            Throwable::class,
            Cloneable::class,
            Number::class,
            Comparable::class,
            Enum::class,
            Annotation::class,
            Iterable::class,
            Iterator::class,
            Collection::class,
            List::class,
            Set::class,
            ListIterator::class,
            Map::class,
            Map.Entry::class,
            Boolean::class,
            Char::class,
            Byte::class,
            Short::class,
            Int::class,
            Float::class,
            Long::class,
            Double::class,
            Nothing::class,
        ).map { (it.qualifiedName ?: error("Reflection issue with $it")) to it }.forEach { (name, klass) ->
            tree[name] = klass
        }
    }

internal object KotlinToJavaTypeMap : Map<String, KClass<*>> by map {
    override fun toString(): String = map.toString()

    override fun equals(other: Any?): Boolean = map == other

    override fun hashCode(): Int = map.hashCode()
}
