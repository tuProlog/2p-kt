package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.ui.gui.Event

internal data class EventImpl<T>(override val name: String, override val event: T) : Event<T>
