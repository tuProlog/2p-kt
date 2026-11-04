package it.unibo.tuprolog.collections.store

/** Merges two sorted, disjoint arrays of positions. */
internal class MergedPositions(
    private val first: IntArray,
    private val second: IntArray,
) : IntIterator() {
    private var i = 0
    private var j = 0

    override fun hasNext(): Boolean = i < first.size || j < second.size

    override fun nextInt(): Int =
        when {
            j >= second.size || (i < first.size && first[i] < second[j]) -> first[i++]
            else -> second[j++]
        }
}
