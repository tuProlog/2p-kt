package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.box
import it.unibo.tuprolog.utils.fullName
import it.unibo.tuprolog.utils.identifier
import it.unibo.tuprolog.utils.name

@Suppress("EqualsOrHashCode")
internal abstract class AbstractRef(value: Any?, tags: Map<String, Any>) : AbstractTerm(tags), ObjectRef {
    @Suppress("PropertyName")
    protected val _value: Any? = value?.let(::box)

    override val hashCodeCache: Int
        get() = _value?.hashCode() ?: 0

    override fun freshCopy(): ObjectRef = this

    override fun freshCopy(scope: Scope): ObjectRef = this

    protected abstract fun copyWithDifferentTags(tags: Map<String, Any>): AbstractRef

    override fun copyWithTags(tags: Map<String, Any>): Term =
        if (this.tags != tags) {
            copyWithDifferentTags(tags)
        } else {
            this
        }

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        this == other

    override fun equals(other: Any?): Boolean =
        asTerm(other)?.asObjectRef()?.let { value === it.value } ?: false

    override fun structurallyEquals(other: Term): Boolean = equals(other)

    private val Any.typeName: String
        get() = this::class.let {
            if (Info.PLATFORM.isJava) it.fullName else it.name
        }

    override fun toString(): String = _value?.let { "<${it.typeName}#${it.identifier}#$it>" } ?: "<null>"

    override val variables: Sequence<Var> = emptySequence()
}
