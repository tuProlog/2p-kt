package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.Optional

/**
 * The [HostInteropBackend] of Kotlin reflection, supporting every object: members are found by name among those of
 * the runtime type of the receiver, and overloads are chosen by how well the arguments convert to their parameters.
 * The members of a [TypeRef] are those of the companion object of its type, if any, or its static ones.
 */
internal object ReflectionBackend : HostInteropBackend {
    override fun supports(receiver: ObjectRef): Boolean = true

    override fun invoke(
        receiver: ObjectRef,
        method: String,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result =
        when (receiver) {
            is TypeRef ->
                when (val companion = receiver.type.companionObjectRef) {
                    is Optional.Some<out Any> -> companion.value.invoke(converter, method, arguments)
                    else -> receiver.type.invoke(converter, method, arguments, null)
                }
            else -> receiver.value.invoke(converter, method, arguments)
        }

    override fun assign(
        receiver: ObjectRef,
        property: String,
        value: Term,
        converter: TermToObjectConverter,
    ) {
        when (receiver) {
            is TypeRef ->
                when (val companion = receiver.type.companionObjectRef) {
                    is Optional.Some<out Any> -> companion.value.assign(converter, property, value)
                    else -> receiver.type.assign(converter, property, value, null)
                }
            else -> receiver.value.assign(converter, property, value)
        }
    }

    override fun create(
        type: ObjectRef,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result {
        require(type is TypeRef) { "Not a type reference: $type" }
        return type.type.create(converter, arguments)
    }
}
