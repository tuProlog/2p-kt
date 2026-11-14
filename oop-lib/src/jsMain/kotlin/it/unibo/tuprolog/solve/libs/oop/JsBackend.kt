package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.MethodInvocationException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import it.unibo.tuprolog.core.Atom as LogicAtom

/**
 * The [HostInteropBackend] of JS objects, supporting every object: a member is the property of the receiver named
 * after it, called with the arguments if it is a function, or read if there are no arguments. JS has no overloads, so
 * nothing is chosen by the types of the arguments, which convert as follows: atoms into strings, integers into
 * numbers, reals into numbers, `true`/`false` into booleans, references into what they reference, and the null
 * reference (or a variable) into `null`. The members of a [TypeRef] are those of the constructor of its type.
 *
 * Kotlin classes are reachable by the names of their members only if exported to JS (see [JsExport]): other members
 * have mangled names. Casts (`as/2`) and aliases (`$/1`) are not supported as arguments.
 */
internal object JsBackend : HostInteropBackend {
    override fun supports(receiver: ObjectRef): Boolean = true

    override fun invoke(
        receiver: ObjectRef,
        method: String,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result {
        val target = receiver.target
        val member = target[method]
        val returned =
            when {
                jsTypeOf(member) == "function" -> member.apply(target, arguments.map(::toJs).toTypedArray())
                arguments.isEmpty() && member !== undefined -> member
                else -> throw MethodInvocationException(receiver.value::class, method, emptyList())
            }
        // functions returning Unit return undefined
        return Result.Value(if (returned === undefined) Unit else returned)
    }

    override fun assign(
        receiver: ObjectRef,
        property: String,
        value: Term,
        converter: TermToObjectConverter,
    ) {
        receiver.target[property] = toJs(value)
    }

    override fun create(
        type: ObjectRef,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result {
        val constructor =
            (type as? TypeRef)?.type?.js ?: throw ConstructorInvocationException(type.value::class, emptyList())
        val construct = js("Reflect.construct")
        return Result.Value(construct(constructor, arguments.map(::toJs).toTypedArray()))
    }

    // the object whose properties are the members: the constructor, for a type
    private val ObjectRef.target: dynamic
        get() = if (this is TypeRef) type.js else value.asDynamic()

    private fun toJs(term: Term): Any? =
        when (term) {
            is NullRef, is Var -> null
            is ObjectRef -> term.value
            is Truth -> term.isTrue
            is LogicAtom -> term.value
            is Integer ->
                term.intValue.toLong().let {
                    if (it in
                        Int.MIN_VALUE..Int.MAX_VALUE
                    ) {
                        it.toInt()
                    } else {
                        it.toDouble()
                    }
                }
            is Real -> term.decimalValue.toDouble()
            else -> throw TermToObjectConversionException(term)
        }
}
