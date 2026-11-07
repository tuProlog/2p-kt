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
    private val previous: MapperCursor<*>? = null,
) : AbstractCursor<R>() {
    constructor(source: AbstractCursor<*>, mapper: (Nothing) -> R) :
        this(source, MapperChain(null, mapper as (Any?) -> Any?))

    private val currentValue: Lazy<R> =
        lazy {
            initializePrevious { it.currentValue }
            chain.last(if (previous == null) source.current else previous.current) as R
        }

    override val current: R
        get() = currentValue.value

    private val nextValue: Lazy<AbstractCursor<out R>> =
        lazy {
            initializePrevious { it.nextValue }
            val sourceNext = source.next
            if (sourceNext.isOver) EmptyCursor else MapperCursor(sourceNext, chain, previous?.next as? MapperCursor<*>)
        }

    override val next: AbstractCursor<out R>
        get() = nextValue.value

    // Initialize shared stages from the source outward, keeping both memoization and bounded stack depth.
    private fun initializePrevious(value: (MapperCursor<*>) -> Lazy<*>) {
        var node = previous
        if (node == null || value(node).isInitialized()) return
        val pending = mutableListOf<MapperCursor<*>>()
        while (node != null && !value(node).isInitialized()) {
            pending.add(node)
            node = node.previous
        }
        for (i in pending.indices.reversed()) {
            value(pending[i]).value
        }
    }

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun <X> map(mapper: (R) -> X): AbstractCursor<out X> =
        MapperCursor(source, MapperChain(chain, mapper as (Any?) -> Any?), this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapperCursor<*>) return false
        return source == other.source && (chain === other.chain || chain.mappers contentEquals other.chain.mappers)
    }

    override fun hashCode(): Int = 31 * source.hashCode() + chain.mappers.contentHashCode()

    /** A persistent, append-only chain of mappers, shared by all the positions of a mapped cursor. */
    internal class MapperChain(
        private val previous: MapperChain?,
        val last: (Any?) -> Any?,
    ) {
        private val size: Int = (previous?.size ?: 0) + 1

        // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
        private val lazyMappers =
            lazy {
                val result = arrayOfNulls<(Any?) -> Any?>(size)
                var node: MapperChain? = this
                var i = size - 1
                while (node != null) {
                    result[i--] = node.last
                    node = node.previous
                }
                result as Array<(Any?) -> Any?>
            }

        /** The mappers, first-applied first; computed once per chain, iteratively. */
        val mappers: Array<(Any?) -> Any?>
            get() = lazyMappers.value
    }
}
