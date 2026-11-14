package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * An [ObjectRef] wrapping a JVM/Kotlin [type], rather than an instance of it, so that Prolog code can construct new
 * instances of it ([create]) and invoke its static or companion-object members ([invoke], [assign]).
 *
 * Like any [ObjectRef], it is equal to the references to the same host value: type references to the same type are
 * equal. It backs the `new_object/2,3` predicates and is what [it.unibo.tuprolog.solve.libs.oop.rules.Alias.forType]
 * and the default type aliases registered by [OOPLib] (`string`, `int`, ...) resolve to.
 *
 * @see TypeFactory
 */
class TypeRef private constructor(
    type: KClass<*>,
    tags: Map<String, Any>,
) : ObjectRef(type, tags) {
    /** The wrapped JVM/Kotlin type this reference gives Prolog code access to. */
    val type: KClass<*>
        get() = value as KClass<*>

    override fun copyWithTags(tags: Map<String, Any>): TypeRef = TypeRef(type, tags)

    override fun toString(): String = "<type:${type.fullName}>"

    companion object {
        /** Wraps [type] into a [TypeRef]. */
        @JvmStatic
        fun of(type: KClass<*>): TypeRef = TypeRef(type.canonical, emptyMap())
    }
}
