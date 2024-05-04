package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ObjectRef : Constant {
    override val isObjectRef: Boolean get() = true

    override val value: Any

    override fun freshCopy(): ObjectRef

    override fun freshCopy(scope: Scope): ObjectRef

    override fun asObjectRef(): ObjectRef = this

    companion object {
        @JvmStatic
        @JsName("of")
        fun of(value: Any?): ObjectRef = TermFactory.default.objectRef(value)
    }
}
