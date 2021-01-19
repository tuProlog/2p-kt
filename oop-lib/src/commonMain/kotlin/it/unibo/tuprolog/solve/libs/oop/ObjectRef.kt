package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.NullRefImpl
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectRefImpl
import kotlin.jvm.JvmStatic

interface ObjectRef : Ref {
    val `object`: Any

    companion object {
        @JvmStatic
        fun of(any: Any?): ObjectRef =
            when (any) {
                null -> NULL
                else -> ObjectRefImpl(any)
            }

        @JvmStatic
        @Suppress("MemberVisibilityCanBePrivate")
        val NULL: NullRef = NullRefImpl
    }
}
