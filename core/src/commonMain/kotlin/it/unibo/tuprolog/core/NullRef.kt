package it.unibo.tuprolog.core

interface NullRef : ObjectRef {
    override val isNullRef: Boolean get() = true

    override val value: Nothing

    override fun freshCopy(): NullRef

    override fun freshCopy(scope: Scope): NullRef

    override fun asNullRef(): NullRef = this
}
