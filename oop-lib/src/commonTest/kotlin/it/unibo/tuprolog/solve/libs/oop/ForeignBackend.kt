package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term

/** A backend for [ForeignHandle]s, standing for a bridge to another runtime: it records what it is asked. */
class ForeignBackend(
    private val reply: String = "foreign",
) : HostInteropBackend {
    val calls = mutableListOf<String>()

    // its objects, and its types, which it creates objects of
    override fun supports(receiver: ObjectRef): Boolean =
        receiver.value is ForeignHandle || (receiver as? TypeRef)?.type == ForeignHandle::class

    override fun invoke(
        receiver: ObjectRef,
        method: String,
        arguments: kotlin.collections.List<Term>,
        converter: TermToObjectConverter,
    ): Result {
        calls += method
        return Result.Value("$reply:${(receiver.value as ForeignHandle).id}.$method/${arguments.size}")
    }

    override fun assign(
        receiver: ObjectRef,
        property: String,
        value: Term,
        converter: TermToObjectConverter,
    ) {
        calls += "$property:="
    }

    override fun create(
        type: ObjectRef,
        arguments: kotlin.collections.List<Term>,
        converter: TermToObjectConverter,
    ): Result = Result.Value(ForeignHandle("new"))
}
