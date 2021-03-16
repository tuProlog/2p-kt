package it.unibo.tuprolog.solve.libs.oop

import java.util.TreeMap
import kotlin.reflect.KClass

object KotlinToJavaTypeMap {
    private val map: Map<String, Class<*>> = TreeMap<String, Class<*>>().also { tree ->
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
            Double::class
        ).forEach { tree[it.qualifiedName!!] = it.java }
    }

    operator fun get(qualifiedName: String): KClass<*>? =
        if (qualifiedName.startsWith("kotlin.")) {
            map[qualifiedName]?.kotlin
        } else {
            null
        }
}
