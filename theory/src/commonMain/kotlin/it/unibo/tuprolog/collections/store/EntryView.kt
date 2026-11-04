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

    fun asReversedSequence(): Sequence<Entry> = (to - 1 downTo from).asSequence().map { array[it]!! }
}
