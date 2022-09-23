package it.unibo.tuprolog.ui.gui

class Runner4Tests(val events: MutableList<Event<Any>>) : Runner {
    companion object {
        val EVENT_UI = Event.of(Runner4Tests::class.simpleName!!, "ui")

        val EVENT_BACKGROUND = Event.of(Runner4Tests::class.simpleName!!, "background")

        val EVENT_IO = Event.of(Runner4Tests::class.simpleName!!, "io")
    }

    override fun ui(action: () -> Unit) {
        events.add(EVENT_UI)
        action()
    }

    override fun background(action: () -> Unit) {
        events.add(EVENT_BACKGROUND)
        action()
    }

    override fun io(action: () -> Unit) {
        events.add(EVENT_IO)
        action()
    }
}
