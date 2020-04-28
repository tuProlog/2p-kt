package it.unibo.tuprolog.theory.rete2

import kotlin.math.min

/** A leaf Rete Node */
internal abstract class AbstractLeafReteNode<E>(
    override val childrenMap: MutableMap<Nothing, ReteNode<*, E>> = mutableMapOf()
) : AbstractReteNode<Nothing, E>(childrenMap) {

    /** Internal data structure to store leaf elements */
    protected abstract val leafElements: MutableList<E>

    override val indexedElements: Sequence<E>
        get() = leafElements.asSequence()

    override fun put(element: E, beforeOthers: Boolean) {
        if (beforeOthers)
            leafElements.add(0, element)
        else
            leafElements.add(element)
    }

    override fun removeWithLimit(element: E, limit: Int): Sequence<E> =
        get(element)
            .take(if (limit > 0) min(limit, leafElements.count()) else leafElements.count())
            .toList()
            .asSequence()
            .also { leafElements.removeAll(it) }

    override fun removeAll(element: E): Sequence<E> =
        get(element)
            .toList()
            .asSequence()
            .also { leafElements.removeAll(it) }

    override fun toString(treefy: Boolean): String =
        if (treefy)
            "$header {${leafElements.joinToString(".\n\t", "\n\t", ".\n")}}"
        else
            toString()
}
