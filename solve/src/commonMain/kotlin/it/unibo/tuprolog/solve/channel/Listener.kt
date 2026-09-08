package it.unibo.tuprolog.solve.channel

/** A callback invoked, by a [Channel], with every element transiting it (see [Channel.addListener]). */
typealias Listener<T> = (T) -> Unit
