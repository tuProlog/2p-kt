package it.unibo.tuprolog.ui.gui.impl

import it.unibo.tuprolog.ui.gui.Event
import it.unibo.tuprolog.ui.gui.History
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.observe.Source
import kotlin.jvm.Volatile

class HistoryImpl<T>(private var itemsSequence: Sequence<T>) : History<T> {

    constructor(vararg items: T) : this(sequenceOf(*items))

    private val itemsCache = Cached.of {
        itemsSequence.distinct().toList()
    }

    override val items: List<T>
        get() = itemsCache.value

    private val size: Int
        get() = items.size

    override var selectedIndex: Int = 0
        get() {
            if (size == 0) {
                throw IllegalStateException("The history is empty")
            }
            return field
        }
        set(value) {
            if (size == 0) {
                throw IllegalStateException("The history is empty")
            }
            field = if (value >= 0) {
                value % size
            } else {
                size + value % size
            }
            raise(History.EVENT_SELECTED, field to items[field])
        }

    override val onAppended: Source<Event<T>> = Source.of()

    override val onSelected: Source<Event<Pair<Int, T>>> = Source.of()

    override fun append(item: T) {
        val repeated = itemsSequence.any { it == item }
        itemsSequence = sequenceOf(item) + itemsSequence.filter { it != item }
        itemsCache.invalidate()
        if (!repeated) raise(History.EVENT_APPENDED, item)
        selectedIndex = 0
    }

    @Volatile
    private var handlingEvent = false

    @Suppress("UNCHECKED_CAST")
    private fun raise(name: String, event: Any?) {
        if (handlingEvent) return
        handlingEvent = true
        when (name) {
            History.EVENT_APPENDED -> onAppended.raise(Event.of(name, event as T))
            History.EVENT_SELECTED -> onSelected.raise(Event.of(name, event as Pair<Int, T>))
            else -> error("Event $event is invalid")
        }
        handlingEvent = false
    }

    override var selected: T
        get() = items[selectedIndex]
        set(value) {
            selectedIndex = items.indexOf(value).takeIf { it >= 0 }
                ?: throw NoSuchElementException("Missing element in history: $value")
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HistoryImpl<*>

        if (items != other.items) return false
        if (selectedIndex != other.selectedIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = items.hashCode()
        result = 31 * result + selectedIndex
        return result
    }

    override fun toString(): String = "HistoryImpl(selectedIndex=$selectedIndex, items=$items)"
}
