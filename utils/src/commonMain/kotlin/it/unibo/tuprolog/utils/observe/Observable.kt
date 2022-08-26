package it.unibo.tuprolog.utils.observe

import it.unibo.tuprolog.utils.observe.impl.ObservableImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Observable<T> {
    @JsName("observers")
    val observers: Collection<Observer<T>>

    @JsName("bind")
    fun bind(observer: Observer<T>): Binding

    @JsName("unbind")
    fun unbind(observer: Observer<T>)

    @JsName("plusAssign")
    operator fun plusAssign(observer: Observer<T>) {
        bind(observer)
    }

    @JsName("minusAssign")
    operator fun minusAssign(observer: Observer<T>) = unbind(observer)

    companion object {
        @JsName("of")
        @JvmStatic
        fun <X> of(vararg consumers: Observer<X>): Observable<X> = ObservableImpl(*consumers)
    }
}
