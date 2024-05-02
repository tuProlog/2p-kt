package it.unibo.tuprolog.core

interface ObjectRef : Constant {
    override val isObjectRef: Boolean get() = true

    override val value: Any

    override fun freshCopy(): ObjectRef

    override fun freshCopy(scope: Scope): ObjectRef

    override fun asObjectRef(): ObjectRef = this
}
