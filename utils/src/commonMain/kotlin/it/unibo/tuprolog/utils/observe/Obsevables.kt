@file:JvmName("Observables")

package it.unibo.tuprolog.utils.observe

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("map")
fun <T, R> Observable<T>.map(f: (T) -> R): Observable<R> {
    val observable = Observable.of<R>()
    val source = observable.toSource()
    bind { source.raise(f(it)) }
    return observable
}

@JsName("filter")
fun <T> Observable<T>.filter(p: (T) -> Boolean): Observable<T> {
    val observable = Observable.of<T>()
    val source = observable.toSource()
    bind { if (p(it)) source.raise(it) }
    return observable
}

@JsName("toSource")
fun <T> Observable<T>.toSource(): Source<T> = Source.of(this)
