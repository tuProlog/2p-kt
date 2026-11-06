package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

/** A snapshot of the content of an [EntryDeque]. */
internal class EntryView(
    private val array: Array<Entry?>,
    private val from: Int,
    private val to: Int,
) {
    val size: Int get() = to - from

    operator fun get(i: Int): Entry = array[from + i]!!

    fun asSequence(): Sequence<Entry> = (from until to).asSequence().map { array[it]!! }

    /** The entries visible at [generation] whose clause satisfies [accept], without intermediate sequences. */
    fun matching(
        generation: Long,
        accept: (Clause) -> Boolean,
    ): Iterator<Entry> =
        object : Iterator<Entry> {
            private var i = from
            private var next: Entry? = null

            override fun hasNext(): Boolean {
                while (next == null && i < to) {
                    val entry = array[i++]!!
                    if (entry.isVisibleAt(generation) && accept(entry.clause)) next = entry
                }
                return next != null
            }

            override fun next(): Entry {
                if (!hasNext()) throw NoSuchElementException()
                return next!!.also { next = null }
            }
        }

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
