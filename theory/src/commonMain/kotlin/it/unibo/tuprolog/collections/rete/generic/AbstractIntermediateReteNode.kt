package it.unibo.tuprolog.collections.rete.generic

import it.unibo.tuprolog.core.Clause

/** A non-leaf Rete Node */
internal abstract class AbstractIntermediateReteNode<K, E : Clause>(
    override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf(),
) : AbstractReteNode<K, E>(children) {
    override val indexedElements: Sequence<E>
        get() = children.asSequence().flatMap { it.value.indexedElements }

    /** Selects correct children, according to [element], onto which to propagate received method calls */
    protected abstract fun selectChildren(element: E): Sequence<ReteNode<*, E>?>

    override fun get(element: E): Sequence<E> = selectChildren(element).flatMap { it?.get(element) ?: emptySequence() }

    override fun removeAll(element: E): Sequence<E> =
        selectChildren(element)
            .toList()
            .flatMap {
                it?.removeAll(element)?.toList() ?: emptyList()
            }.asSequence()

    /** Retrieves from receiver map those values of [ChildNodeType] that have a key respecting [keyFilter] */
    protected fun <ChildNodeType> MutableMap<K, ReteNode<*, E>>.retrieve(
        keyFilter: (K) -> Boolean,
        typeChecker: (ReteNode<*, E>) -> Boolean,
        caster: (ReteNode<*, E>) -> ChildNodeType,
    ): Sequence<ChildNodeType> =
        filterValues(typeChecker)
            .filterKeys(keyFilter)
            .values
            .asSequence()
            .map(caster)

    /**
     *  A [Sequence.fold] function that stops when accumulated operation results' count becomes greater or equal to [limit];
     *
     * if negative [limit] this behaves actually like normal [Sequence.fold]
     */
    protected inline fun <T, R : Iterable<*>> Sequence<T>.foldWithLimit(
        initial: R,
        limit: Int,
        operation: (acc: R, T) -> R,
    ) = if (limit < 0) {
        fold(initial, operation)
    } else {
        fold(initial) { accumulator, element ->
            if (accumulator.count() < limit) {
                operation(accumulator, element)
            } else {
                return@foldWithLimit accumulator
            }
        }
    }
}
