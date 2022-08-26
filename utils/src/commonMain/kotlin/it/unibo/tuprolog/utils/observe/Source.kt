package it.unibo.tuprolog.utils.observe

import it.unibo.tuprolog.utils.observe.impl.SourceImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface Source<T> : Observable<T> {
    @JsName("raise")
    fun raise(value: T)

    companion object {
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun <X> of(observable: Observable<X> = Observable.of()): Source<X> =
            when (observable) {
                is Source<X> -> observable
                else -> SourceImpl(observable)
            }
    }
}
