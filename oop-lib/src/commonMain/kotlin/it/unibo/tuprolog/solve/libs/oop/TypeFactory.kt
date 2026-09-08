package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.TypeFactoryImpl
import it.unibo.tuprolog.utils.Optional
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * Resolves a fully-qualified type name (as it would appear as an [it.unibo.tuprolog.core.Atom] in
 * Prolog code, e.g. in a `cast/3` expression `X as 'java.util.ArrayList'` or in `new_object/2,3`)
 * into a [KClass], backing type-name lookups performed by [TermToObjectConverter] and the `type/2`
 * predicate ([it.unibo.tuprolog.solve.libs.oop.primitives.Type]).
 *
 * On the JVM this also understands Kotlin's own standard-library names (e.g. `kotlin.String`,
 * `kotlin.collections.List`) via an internal name map, since `Class.forName` alone only knows
 * about their JVM/Java counterparts.
 *
 * @see it.unibo.tuprolog.solve.libs.oop.kClassFromName
 */
interface TypeFactory {
    companion object {
        /** The default [TypeFactory], backed by platform-specific class-loading facilities. */
        @JvmStatic
        val default: TypeFactory = TypeFactoryImpl()
    }

    /** Resolves [typeName] into a [KClass], or `null` if no such type could be found. */
    fun typeFromName(typeName: String): KClass<*>?

    /** Like [typeFromName], wrapping the result into a [TypeRef]. */
    fun typeRefFromName(typeName: String): TypeRef? = Optional.of(typeFromName(typeName)).map { TypeRef.of(it) }.value
}
