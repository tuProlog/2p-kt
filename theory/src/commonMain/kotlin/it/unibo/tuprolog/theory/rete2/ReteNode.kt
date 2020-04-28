package it.unibo.tuprolog.theory.rete2

/** A class modeling a Rete Tree Node */
internal interface ReteNode<K, E> {

    /** All direct children of this node */
    val childrenMap: MutableMap<K, ReteNode<*, E>>

    //val childrenList: MutableList<ReteNode<*, E>>

    /** All indexed elements by this node and its children */
    val indexedElements: Sequence<E>

    /** Indexes a new element under this node */
    fun put(element: E, beforeOthers: Boolean)

    /** Indexes a new element under this node, after the others */
    fun put(element: E) =
        put(element, false)

    /** Gets a sequence of matching elements starting from give one */
    fun get(element: @UnsafeVariance E): Sequence<E>

    /**
     * Removes elements matching given [element], respecting provided [limit], and returns them.
     * A negative limit will be considered as if there's no limit, i.e. all matching elements will be removed
     */
    fun remove(element: @UnsafeVariance E, limit: Int): Sequence<E>

    /**
     * Removes one element matching given [element], and returns it;
     */
    fun remove(element: @UnsafeVariance E): Sequence<E> =
        remove(element, 1)

    /** Removes all elements matching given one */
    fun removeAll(element: @UnsafeVariance E): Sequence<E>

    /** Creates a deep copy of the tree index; it doesn't copy indexed elements */
    fun deepCopy(): ReteNode<K, E>

    /** An enhanced toString that can print nodes in Tree fashion */
    fun toString(treefy: Boolean): String

    override fun toString(): String
}
