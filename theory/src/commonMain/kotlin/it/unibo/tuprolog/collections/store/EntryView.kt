package it.unibo.tuprolog.collections.store

/** A snapshot of the content of an [EntryDeque]. */
internal class EntryView(
    private val array: Array<Entry?>,
    private val from: Int,
    private val to: Int,
) {
    val size: Int get() = to - from

    operator fun get(i: Int): Entry = array[from + i]!!

    fun asSequence(): Sequence<Entry> = (from until to).asSequence().map { array[it]!! }

    /** The entries visible at [generation], mapped through [transform], without intermediate sequences. */
    fun <T> visibleAt(
        generation: Long,
        transform: (Entry) -> T,
    ): Iterator<T> =
        object : Iterator<T> {
            private var i = nextVisible(from)

            private fun nextVisible(start: Int): Int {
                var j = start
                while (j < to && !array[j]!!.isVisibleAt(generation)) j++
                return j
            }

            override fun hasNext(): Boolean = i < to

            override fun next(): T {
                if (i >= to) throw NoSuchElementException()
                return transform(array[i]!!).also { i = nextVisible(i + 1) }
            }
        }

    fun asReversedSequence(): Sequence<Entry> = (to - 1 downTo from).asSequence().map { array[it]!! }
}
