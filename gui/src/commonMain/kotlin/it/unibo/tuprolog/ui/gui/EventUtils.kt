@file:JvmName("EventUtils")

package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.utils.observe.Source
import kotlin.jvm.JvmName

fun <T> Source<Event<T>>.raise(name: String, event: T) {
    raise(Event.of(name, event))
}

fun <T> Source<SolverEvent<T>>.raise(name: String, event: T, executionContext: ExecutionContextAware) {
    raise(SolverEvent.copyOf(name, event, executionContext))
}
