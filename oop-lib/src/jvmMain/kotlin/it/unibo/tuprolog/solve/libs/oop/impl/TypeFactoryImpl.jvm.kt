package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.solve.libs.oop.CLASS_NAME_PATTERN
import it.unibo.tuprolog.solve.libs.oop.ID
import it.unibo.tuprolog.solve.libs.oop.KotlinToJavaTypeMap
import it.unibo.tuprolog.solve.libs.oop.TypeFactory
import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional
import kotlin.reflect.KClass

internal actual class TypeFactoryImpl : TypeFactory {
    companion object {
        private const val DEFAULT_CACHE_SIZE = 32
        private val CLASS_NAME_PATTERN: Regex = "^$ID(\\.$ID(\\$$ID)*)*$".toRegex()
    }

    private val classCache = Cache.simpleLru<String, Optional<out KClass<*>>>(DEFAULT_CACHE_SIZE)

    @Suppress("ReturnCount")
    actual fun kClassFromName(qualifiedName: String): Optional<out KClass<*>> {
        require(CLASS_NAME_PATTERN.matches(qualifiedName)) {
            "`$qualifiedName` must match ${CLASS_NAME_PATTERN.pattern} while it doesn't"
        }
        return classCache.getOrSet(qualifiedName) { kClassFromNameImpl(qualifiedName) }
    }

    @Suppress("ReturnCount")
    private fun kClassFromNameImpl(qualifiedName: String): Optional<out KClass<*>> {
        val kotlinKlass = KotlinToJavaTypeMap[qualifiedName]
        return if (kotlinKlass != null) {
            Optional.of(kotlinKlass)
        } else {
            javaClassForName(qualifiedName)?.let { return Optional.some(it.kotlin) }
            var lastDot = qualifiedName.lastIndexOf('.')
            var name = qualifiedName
            while (lastDot >= 0) {
                name = name.replaceAt(lastDot, '$')
                javaClassForName(name)?.let { return Optional.some(it.kotlin) }
                lastDot = name.lastIndexOf('.')
            }
            Optional.none()
        }
    }

    private fun String.replaceAt(
        index: Int,
        char: Char,
    ): String {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("Index out of bounds: $index")
        return substring(0, index) + char + substring(index + 1)
    }

    @Suppress("SwallowedException")
    private fun javaClassForName(qualifiedName: String): Class<*>? =
        try {
            Class.forName(qualifiedName)
        } catch (e: ClassNotFoundException) {
            null
        }

    actual override fun typeFromName(typeName: String): KClass<*>? = kClassFromName(typeName).value
}
