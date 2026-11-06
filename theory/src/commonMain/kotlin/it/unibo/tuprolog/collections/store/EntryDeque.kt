package it.unibo.tuprolog.collections.store

/** An array-backed deque of [Entry]s, appending in place at both ends. */
internal class EntryDeque private constructor(
    private var array: Array<Entry?>,
    private var head: Int,
) {
    private var tail: Int = head

    constructor() : this(arrayOfNulls(INITIAL_CAPACITY), INITIAL_CAPACITY / 2)

    val size: Int get() = tail - head

    /** How many of this deque's entries were removed from the store, but are still here. */
    var dead: Int = 0

    fun addLast(entry: Entry) {
        if (tail == array.size) grow()
        array[tail++] = entry
    }

    fun addFirst(entry: Entry) {
        if (head == 0) grow()
        array[--head] = entry
    }

    private fun grow() {
        val size = size
        val capacity = 2 * size + INITIAL_CAPACITY
        val newHead = (capacity - size) / 2
        val newArray = arrayOfNulls<Entry>(capacity)
        array.copyInto(newArray, newHead, head, tail)
        array = newArray
        head = newHead
        tail = newHead + size
    }

    fun view(): EntryView = EntryView(array, head, tail)

    fun alive(): Sequence<Entry> = view().asSequence().filter { it.isAlive }

    companion object {
        private const val INITIAL_CAPACITY = 8

        fun of(entries: Sequence<Entry>): EntryDeque =
            EntryDeque().also { deque -> entries.forEach { deque.addLast(it) } }
    }
}
