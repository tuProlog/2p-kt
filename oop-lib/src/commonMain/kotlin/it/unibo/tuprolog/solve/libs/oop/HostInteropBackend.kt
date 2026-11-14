package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Term

/**
 * A way of reaching the members of host objects, for the predicates of [OOPLib]: JVM reflection, the properties and
 * functions of JS objects, or a bridge towards another runtime, e.g. Python.
 *
 * The library tries the backends injected via [OOPLib.withBackends], in order, then the platform's own one: the first
 * one that [supports] a receiver handles it. Receivers are never `null`: calls on the null reference fail before any
 * backend is chosen. Type references ([TypeRef]) are receivers too: of static members, and of [create].
 *
 * A backend for another runtime (as `2ppy` would register for Python objects) wraps that runtime's objects in opaque
 * handles, holds them in [ObjectRef]s (which compare their handles by identity, so a handle must stand for one foreign
 * object), and keeps them alive as long as the handles are. It is then given:
 * - the name of the member and the arguments as logic terms, to convert as it sees fit, e.g. with [converter] or into
 *   the other runtime's values (keyword arguments are not supported: arguments are positional);
 * - the duty to return a [Result]: [Result.Value] of what the member returned (a host object, or a handle, which
 *   [ObjectToTermConverter] turns into a reference), or [Result.None] for no value;
 * - the duty to throw [it.unibo.tuprolog.solve.libs.oop.exceptions.OopException]s for missing members or unconvertible
 *   arguments, which become logic errors; any other exception becomes a system error.
 */
interface HostInteropBackend {
    /** Whether this backend reaches the members of the host value of [receiver] (never a null reference). */
    fun supports(receiver: ObjectRef): Boolean

    /**
     * Invokes the member named [method] of the host value of [receiver] with [arguments], or reads the property named
     * [method] if there are no arguments.
     */
    fun invoke(
        receiver: ObjectRef,
        method: String,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result

    /** Assigns [value] to the property named [property] of the host value of [receiver]. */
    fun assign(
        receiver: ObjectRef,
        property: String,
        value: Term,
        converter: TermToObjectConverter,
    )

    /** Creates an instance of the type [type] stands for, with [arguments]. */
    fun create(
        type: ObjectRef,
        arguments: List<Term>,
        converter: TermToObjectConverter,
    ): Result

    companion object {
        /** The backend of the current platform: reflection on the JVM, properties and functions on JS. */
        val platform: HostInteropBackend
            get() = platformBackend
    }
}

/** The backend of the current platform. */
internal expect val platformBackend: HostInteropBackend
