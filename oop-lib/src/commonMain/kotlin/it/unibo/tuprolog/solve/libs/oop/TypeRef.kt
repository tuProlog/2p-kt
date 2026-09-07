package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.impl.TypeRefImpl
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * A [Ref] wrapping a JVM/Kotlin [type] (rather than an instance of it, as [ObjectRef] does), so
 * that Prolog code can construct new instances of it ([create]) and invoke its static members /
 * companion object members via the inherited [Ref.invoke] and [Ref.assign].
 *
 * A [TypeRef] renders as the atom `<type:<fully-qualified-type-name>>`. It backs the `new_object/2,3`
 * predicates ([it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3],
 * [it.unibo.tuprolog.solve.libs.oop.rules.NewObject2]) and is what
 * [it.unibo.tuprolog.solve.libs.oop.rules.Alias.forType] and the default type aliases registered
 * by [OOPLib] (`string`, `int`, ...) resolve to.
 *
 * @see ObjectRef
 * @see TypeFactory
 */
interface TypeRef : Ref {
    /** The wrapped JVM/Kotlin type this reference gives Prolog code access to. */
    val type: KClass<*>

    /**
     * Invokes the (possibly overloaded) constructor of [type] whose formal parameters best match
     * [arguments] (converted via [objectConverter]), yielding a [Result] wrapping the freshly
     * created instance.
     *
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.ConstructorInvocationException if no
     * public constructor of [type] accepts arguments compatible with [arguments].
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException if the invoked
     * constructor throws.
     * @throws it.unibo.tuprolog.solve.libs.oop.exceptions.RuntimePermissionException if reflective
     * access to the constructor is denied by the runtime.
     */
    fun create(
        objectConverter: TermToObjectConverter,
        vararg arguments: Term,
    ): Result = create(objectConverter, listOf(*arguments))

    /** @see create */
    fun create(
        objectConverter: TermToObjectConverter,
        arguments: List<Term>,
    ): Result

    /** @see create */
    fun create(
        objectConverter: TermToObjectConverter,
        arguments: Iterable<Term>,
    ): Result = create(objectConverter, arguments.toList())

    /** @see create */
    fun create(
        objectConverter: TermToObjectConverter,
        arguments: Sequence<Term>,
    ): Result = create(objectConverter, arguments.toList())

    /** Like [create], but converting [arguments] via [TermToObjectConverter.default]. */
    fun create(vararg arguments: Term): Result = create(TermToObjectConverter.default, listOf(*arguments))

    /** @see create */
    fun create(arguments: List<Term>): Result = create(TermToObjectConverter.default, arguments)

    /** @see create */
    fun create(arguments: Iterable<Term>): Result = create(TermToObjectConverter.default, arguments.toList())

    /** @see create */
    fun create(arguments: Sequence<Term>): Result = create(TermToObjectConverter.default, arguments.toList())

    companion object {
        /** Wraps [type] into a [TypeRef]. */
        @JvmStatic
        fun of(type: KClass<*>): TypeRef = TypeRefImpl(type)
    }
}
