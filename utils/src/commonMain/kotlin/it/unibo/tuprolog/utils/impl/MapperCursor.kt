package it.unibo.tuprolog.utils.impl

/**
 * A lazily-mapped cursor. Mapping a [MapperCursor] again does not wrap it: the new mapper is appended to
 * the [chain] applied to the same [source], so `k` composed mappings cost a loop of `k` calls per element,
 * not `k` nested cursors (and native stack frames). Both [current] and [next] are memoized.
 */
@Suppress("UNCHECKED_CAST")
internal class MapperCursor<R> private constructor(
    private val source: AbstractCursor<*>,
    private val chain: MapperChain,
) : AbstractCursor<R>() {
    constructor(source: AbstractCursor<*>, mapper: (Nothing) -> R) :
        this(source, MapperChain(null, mapper as (Any?) -> Any?))

    override val current: R by lazy {
        var value: Any? = source.current
        for (mapper in chain.mappers) {
            value = mapper(value)
        }
        value as R
    }

    override val next: AbstractCursor<out R> by lazy {
        val sourceNext = source.next
        if (sourceNext.isOver) EmptyCursor else MapperCursor(sourceNext, chain)
    }

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun <X> map(mapper: (R) -> X): AbstractCursor<out X> =
        MapperCursor(source, MapperChain(chain, mapper as (Any?) -> Any?))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapperCursor<*>) return false
        return source == other.source && chain === other.chain
    }

    override fun hashCode(): Int = 31 * source.hashCode() + chain.hashCode()

    /** A persistent, append-only chain of mappers, shared by all the positions of a mapped cursor. */
    internal class MapperChain(
        private val previous: MapperChain?,
        private val last: (Any?) -> Any?,
    ) {
        private val size: Int = (previous?.size ?: 0) + 1

        /** The mappers, first-applied first; computed once per chain, iteratively. */
        val mappers: Array<(Any?) -> Any?> by lazy {
            val result = arrayOfNulls<(Any?) -> Any?>(size)
            var node: MapperChain? = this
            var i = size - 1
            while (node != null) {
                result[i--] = node.last
                node = node.previous
            }
            result as Array<(Any?) -> Any?>
        }
    }
}
