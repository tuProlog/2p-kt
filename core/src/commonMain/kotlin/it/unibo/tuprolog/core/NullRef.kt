package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

interface NullRef : ObjectRef {
    override val isNullRef: Boolean get() = true

    override val value: Nothing

    override fun freshCopy(): NullRef

    override fun freshCopy(scope: Scope): NullRef

    override fun asNullRef(): NullRef = this

    companion object {
        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): NullRef = TermFactory.default.nullRef()

        @JvmStatic
        @JsName("instance")
        @get:JvmName("instance")
        val instance: NullRef
            get() = TermFactory.default.nullRef()
    }
}
