package it.unibo.tuprolog.utils.observe.impl

import it.unibo.tuprolog.utils.observe.Binding
import it.unibo.tuprolog.utils.observe.Observable
import it.unibo.tuprolog.utils.observe.Observer

internal data class ObservableImpl<T>(private var _observers: Set<Observer<T>>) : Observable<T> {

    constructor(vararg observers: Observer<T>) : this(setOf(*observers))

    override val observers: Collection<Observer<T>>
        get() = _observers

    override fun unbind(observer: Observer<T>) {
        _observers = _observers - observer
    }

    override fun bind(observer: Observer<T>): Binding {
        _observers = _observers + observer
        return Binding { unbind(observer) }
    }
}
