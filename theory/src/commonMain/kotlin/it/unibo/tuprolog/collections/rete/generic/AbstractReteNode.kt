package it.unibo.tuprolog.collections.rete.generic

import kotlin.jvm.JvmStatic

/** Abstract base class for Rete Tree nodes */
internal abstract class AbstractReteNode<K, E>(override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf()) :
    ReteNode<K, E> {

    /** Description for current Rete Tree Node */
    protected abstract val header: String

    override fun remove(element: E, limit: Int): Sequence<E> = when (limit) {
        0 -> emptySequence()
        else -> removeWithLimit(element, limit)
    }

    /** Called when a non-zero-limit removal is required inside a node */
    protected abstract fun removeWithLimit(element: E, limit: Int): Sequence<E>

    override fun removeAll(element: E): Sequence<E> = remove(element, Int.MAX_VALUE)

    override fun toString(treefy: Boolean): String =
        if (treefy) {
            "$header {" +
                children.values.joinToString(",\n\t", "\n\t", "\n") {
                    it.toString(treefy).replace("\n", "\n\t")
                } + "}"
        } else {
            "$header {${children.values.joinToString(",") {
                it.toString()
            }}"
        }

    override fun toString(): String {
        return toString(false)
    }

    companion object {

        /** Utility function to deeply copy a MutableMap */
        @JvmStatic
        protected inline fun <K, V> MutableMap<K, V>.deepCopy(
            deepCopyKey: (K) -> K,
            deepCopyValue: (V) -> V
        ): MutableMap<K, V> =
            entries.map { deepCopyKey(it.key) to deepCopyValue(it.value) }.toMap(mutableMapOf())
    }
}
