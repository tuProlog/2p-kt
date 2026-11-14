package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An opaque, ground, immutable [Constant] wrapping a host-platform value (the value itself may be mutable).
 *
 * The standard sub-types are [ObjectRef] (wrapping a non-null value, compared by identity) and [NullRef] (wrapping
 * `null`). This interface is deliberately open, so that other modules can define further kinds of references.
 * References are neither atoms nor numbers, they are formatted as opaque tokens, and they cannot be parsed back nor
 * portably serialized: a live host object is not a portable value.
 *
 * References hold their values strongly, for as long as the reference term itself is reachable.
 */
interface Ref : Constant {
    override val isRef: Boolean
        get() = true

    override fun asRef(): Ref = this

    override val value: Any?

    override val variables: Sequence<Var>
        get() = emptySequence()

    override val isGround: Boolean
        get() = true

    override fun freshCopy(): Ref

    override fun freshCopy(scope: Scope): Ref

    companion object {
        /**
         * Wraps [value] into a [Ref]: [NullRef.instance] if [value] is `null`, an [ObjectRef] otherwise.
         * Notice that scalars (e.g. strings, numbers) are wrapped as opaque references too, whose identity semantics
         * depend on the platform: use the ordinary term factories (e.g. [Atom.of], [Integer.of]) for those.
         * @throws IllegalArgumentException if [value] is a [Term]
         */
        @JvmStatic
        @JsName("of")
        fun of(value: Any?): Ref = if (value == null) NullRef.instance else ObjectRef.of(value)
    }
}
