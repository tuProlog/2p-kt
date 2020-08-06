package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.libs.oop.impl.NullRefImpl
import it.unibo.tuprolog.solve.libs.oop.impl.ObjectRefImpl

interface ObjectRef : Ref {
    val `object`: Any

    companion object {
        fun of(any: Any?): ObjectRef =
            when(any) {
                null -> NULL
                else -> ObjectRefImpl(any)
            }

        @Suppress("MemberVisibilityCanBePrivate")
        val NULL: NullRef = NullRefImpl
    }
}