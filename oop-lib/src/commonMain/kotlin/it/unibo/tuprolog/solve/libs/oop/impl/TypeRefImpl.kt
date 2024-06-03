package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class TypeRefImpl(override val value: KClass<*>) : TypeRef, ObjectRef by ObjectRef.of(value) {
    override fun freshCopy(): TypeRef = this

    override fun freshCopy(scope: Scope): TypeRef = this
}
