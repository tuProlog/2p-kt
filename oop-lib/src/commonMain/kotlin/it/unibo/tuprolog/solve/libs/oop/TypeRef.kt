package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Scope
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

interface TypeRef : ObjectRef {
    val isTypeRef: Boolean get() = true

    override val value: KClass<*>

    override fun freshCopy(): TypeRef

    override fun freshCopy(scope: Scope): TypeRef

    fun asTypeRef(): TypeRef = this

    fun castToTypeRef(): TypeRef = this

    companion object {
        @JvmStatic
        @JsName("of")
        fun of(value: KClass<*>): TypeRef = TODO()
    }
}
