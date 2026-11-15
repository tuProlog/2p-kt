package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.core.impl.ObjectRefImpl
import it.unibo.tuprolog.core.impl.asTerm
import it.unibo.tuprolog.core.impl.hostIdentityHashCode
import it.unibo.tuprolog.core.impl.sameHost
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Ref] wrapping a non-null host [value], which is never a [Term].
 *
 * Two [ObjectRef]s are equal (hence unify) if and only if they wrap the very same host object, regardless of their
 * tags, of their concrete class, and of the wrapped object's own `equals`. On JS, primitive values (e.g. strings,
 * numbers) are the same host value whenever they are equal.
 *
 * This is an extensible class, rather than an interface, so that the invariant on [value] holds for sub-classes too.
 * Sub-classes must only implement [copyWithTags].
 */
abstract class ObjectRef protected constructor(
    value: Any,
    final override val tags: Map<String, Any> = emptyMap(),
) : Ref {
    final override val value: Any = value

    init {
        require(value !is Term) { "Cannot wrap a logic term in an ObjectRef: $value" }
    }

    final override val isObjectRef: Boolean
        get() = true

    final override fun asObjectRef(): ObjectRef = this

    /** Creates a copy of this reference, wrapping the same [value], but carrying [tags]. */
    protected abstract fun copyWithTags(tags: Map<String, Any>): ObjectRef

    final override fun replaceTags(tags: Map<String, Any>): ObjectRef =
        if (this.tags == tags) this else copyWithTags(tags)

    final override fun freshCopy(): ObjectRef = this

    final override fun freshCopy(scope: Scope): ObjectRef = this

    final override fun apply(substitution: Substitution): Term =
        if (substitution.isSuccess) this else throw SubstitutionApplicationException(this, substitution)

    private fun sameReferenceAs(other: Term?): Boolean =
        other?.asObjectRef()?.let { this === it || sameHost(value, it.value) } ?: false

    final override fun equals(other: Any?): Boolean = sameReferenceAs(asTerm(other))

    final override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = sameReferenceAs(other)

    final override fun structurallyEquals(other: Term): Boolean = sameReferenceAs(other)

    final override fun hashCode(): Int = hostIdentityHashCode(value)

    /** An opaque token, e.g. `<object:MyClass@1b6d3586>`, which never includes the wrapped value's `toString`. */
    override fun toString(): String =
        "<object:${value::class.simpleName ?: "?"}@${hostIdentityHashCode(value).toUInt().toString(HEX)}>"

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitObjectRef(this)

    companion object {
        private const val HEX = 16

        /**
         * Wraps the non-null host [value] into an [ObjectRef].
         * @throws IllegalArgumentException if [value] is a [Term]
         */
        @JvmStatic
        @JsName("of")
        fun of(value: Any): ObjectRef = ObjectRefImpl(value)
    }
}
