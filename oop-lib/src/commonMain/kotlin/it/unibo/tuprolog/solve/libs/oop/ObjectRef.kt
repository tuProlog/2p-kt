package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.NullRefImpl
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectRefImpl
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * A [Ref] wrapping a live, non-`null` JVM/Kotlin [object] instance, so that it can flow through
 * Prolog terms and be the target of `invoke_method/3`, `assign/3`, and friends.
 *
 * An [ObjectRef] renders as the atom `<object:<fully-qualified-type-name>#<identity-hash>>` (see
 * [nameOf]), so two [ObjectRef]s over `equal`-but-distinct objects are different Prolog terms --
 * reference identity, not value equality, is what backs unification here.
 *
 * @see TypeRef
 * @see NULL
 */
interface ObjectRef : Ref {
    /** The wrapped JVM/Kotlin object this reference gives Prolog code access to. */
    @Suppress("ktlint:standard:property-naming")
    val `object`: Any

    companion object {
        /** The display name an [ObjectRef] (or [NULL], for `null`) wrapping [any] would have. */
        @JvmStatic
        fun nameOf(any: Any?): String =
            when (any) {
                null -> nameOf(Nothing::class, "null")
                else -> nameOf(any::class, any.identifier)
            }

        private fun nameOf(
            type: KClass<*>,
            identifier: String,
        ): String = "<object:${type.fullName}#$identifier>"

        /** Wraps [any] into an [ObjectRef], or into [NULL] if [any] is `null`. */
        @JvmStatic
        fun of(any: Any?): ObjectRef =
            when (any) {
                null -> NULL
                else -> ObjectRefImpl(any)
            }

        /** The singleton [NullRef], i.e. the [ObjectRef] representing a `null` JVM/Kotlin value. */
        @JvmField
        @Suppress("MemberVisibilityCanBePrivate")
        val NULL: NullRef = NullRefImpl
    }
}
