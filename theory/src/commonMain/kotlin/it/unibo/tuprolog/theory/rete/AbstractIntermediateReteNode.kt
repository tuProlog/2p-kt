package it.unibo.tuprolog.theory.rete

/** A non-leaf Rete Node */
internal abstract class AbstractIntermediateReteNode<K, E>(override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf()) :
    AbstractReteNode<K, E>(children) {

    override val indexedElements: Sequence<E>
        get() = children.asSequence().flatMap { it.value.indexedElements }

    /** Selects correct children, according to [element], onto which to propagate received method calls */
    protected abstract fun selectChildren(element: E): Sequence<ReteNode<*, E>?>

    override fun get(element: E): Sequence<E> =
        selectChildren(element).flatMap { it?.get(element) ?: emptySequence() }

    override fun removeAll(element: E): Sequence<E> =
        selectChildren(element).toList().flatMap {
            it?.removeAll(element)?.toList() ?: emptyList()
        }.asSequence()

    /** Retrieves from receiver map those values of [ChildNodeType] that have a key respecting [keyFilter] */
    protected inline fun <reified ChildNodeType> MutableMap<K, ReteNode<*, E>>.retrieve(keyFilter: (K) -> Boolean) =
        filterValues { node -> node is ChildNodeType }
            .filterKeys(keyFilter)
            .values.asSequence()

    /**
     *  A [Sequence.fold] function that stops when accumulated operation results' count becomes greater or equal to [limit];
     *
     * if negative [limit] this behaves actually like normal [Sequence.fold]
     */
    protected inline fun <T, R : Iterable<*>> Sequence<T>.foldWithLimit(
        initial: R,
        limit: Int,
        operation: (acc: R, T) -> R
    ) =
        if (limit < 0) fold(initial, operation)
        else
            fold(initial) { accumulator, element ->
                if (accumulator.count() < limit)
                    operation(accumulator, element)
                else
                    return@foldWithLimit accumulator
            }
}
