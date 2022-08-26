package it.unibo.tuprolog.utils.observe.impl

import it.unibo.tuprolog.utils.observe.Observable
import it.unibo.tuprolog.utils.observe.Source

data class SourceImpl<T>(private val observable: Observable<T>) : Source<T>, Observable<T> by observable {
    override fun raise(value: T) {
        for (observer in observers) {
            observer.notify(value)
        }
    }
}
