package it.unibo.tuprolog.theory.rete

/**
 * A class modeling a Rete Tree Node
 */
internal interface ReteNode<Key, Element> {

    /** All direct children of this node */
    val children: MutableMap<Key, ReteNode<*, Element>>

    /** All indexed elements by this node and its children */
    val indexedElements: Sequence<Element>

    /** Indexes a new element under this node */
    fun put(element: Element, beforeOthers: Boolean = false)

    /** Gets a sequence of matching elements starting from give one */
    fun get(element: @UnsafeVariance Element): Sequence<Element>

    /**
     * Removes elements matching given [element], respecting provided [limit], and returns them;
     *
     * A negative limit will be considered as if there's no limit, i.e. all matching elements will be removed
     */
    fun remove(element: @UnsafeVariance Element, limit: Int = 1): Sequence<Element>

    /** Removes all elements matching given one */
    fun removeAll(clause: @UnsafeVariance Element): Sequence<Element>

    /** Creates a deep copy of the tree index; it doesn't copy indexed elements */
    fun deepCopy(): ReteNode<Key, Element>

    /** An enhanced toString that can print nodes in Tree fashion */
    fun toString(treefy: Boolean = false): String
}
