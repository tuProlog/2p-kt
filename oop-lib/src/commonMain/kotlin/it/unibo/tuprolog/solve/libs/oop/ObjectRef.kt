package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.NullRefImpl
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectRefImpl
import it.unibo.tuprolog.utils.fullName
import it.unibo.tuprolog.utils.identifier
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

interface ObjectRef : Ref {
    @Suppress("ktlint:standard:property-naming")
    val `object`: Any

    companion object {
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

        @JvmStatic
        fun of(any: Any?): ObjectRef =
            when (any) {
                null -> NULL
                else -> ObjectRefImpl(any)
            }

        @JvmField
        @Suppress("MemberVisibilityCanBePrivate")
        val NULL: NullRef = NullRefImpl
    }
}
