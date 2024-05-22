package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.solve.libs.oop.TypeRef
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class TypeRefImpl(override val value: KClass<*>) : TypeRef, ObjectRef by ObjectRef.of(value) {

//    override fun create(
//        objectConverter: Objectifier,
//        arguments: List<Term>,
//    ): Result {
//        return type.create(objectConverter, arguments)
//    }
//
//    override fun invoke(
//        objectConverter: Objectifier,
//        methodName: String,
//        arguments: List<Term>,
//    ): Result =
//        when (val companionObjectRef = type.companionObjectRef) {
//            is Optional.Some<out Any> -> companionObjectRef.value.invoke(objectConverter, methodName, arguments)
//            else -> type.invoke(objectConverter, methodName, arguments, null)
//        }

    //    override fun assign(
//        objectConverter: Objectifier,
//        propertyName: String,
//        value: Term,
//    ): Boolean {
//        when (val companionObjectRef = type.companionObjectRef) {
//            is Optional.Some<out Any> -> companionObjectRef.value.assign(objectConverter, propertyName, value)
//            else -> type.assign(objectConverter, propertyName, value, null)
//        }
//        return true
//    }
    override fun freshCopy(): TypeRef = this

    override fun freshCopy(scope: Scope): TypeRef = this
}
