package it.unibo.tuprolog.collections.rete.generic

import it.unibo.tuprolog.collections.rete.generic.set.ArgNode
import it.unibo.tuprolog.collections.rete.generic.set.ArityNode
import it.unibo.tuprolog.collections.rete.generic.set.DirectiveNode
import it.unibo.tuprolog.collections.rete.generic.set.FunctorNode
import it.unibo.tuprolog.collections.rete.generic.set.NoArgsNode
import it.unibo.tuprolog.collections.rete.generic.set.RootNode
import it.unibo.tuprolog.collections.rete.generic.set.RuleNode
import it.unibo.tuprolog.core.Clause
import kotlin.jvm.JvmStatic

/** Abstract base class for Rete Tree nodes */
internal abstract class AbstractReteNode<K, E : Clause>(
    override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf(),
) : ReteNode<K, E> {
    override val isFunctorNode: Boolean
        get() = false

    override fun asFunctorNode(): FunctorNode? = null

    override fun castToFunctorNode(): FunctorNode =
        asFunctorNode() ?: throw ClassCastException("Cannot cast $this to ${FunctorNode::class.simpleName}")

    override val isArgNode: Boolean
        get() = false

    override fun asArgNode(): ArgNode? = null

    override fun castToArgNode(): ArgNode =
        asArgNode() ?: throw ClassCastException("Cannot cast $this to ${ArgNode::class.simpleName}")

    override val isArityNode: Boolean
        get() = false

    override fun asArityNode(): ArityNode? = null

    override fun castToArityNode(): ArityNode =
        asArityNode() ?: throw ClassCastException("Cannot cast $this to ${ArityNode::class.simpleName}")

    override val isDirectiveNode: Boolean
        get() = false

    override fun asDirectiveNode(): DirectiveNode? = null

    override fun castToDirectiveNode(): DirectiveNode =
        asDirectiveNode() ?: throw ClassCastException("Cannot cast $this to ${DirectiveNode::class.simpleName}")

    override val isRootNode: Boolean
        get() = false

    override fun asRootNode(): RootNode? = null

    override fun castToRootNode(): RootNode =
        asRootNode() ?: throw ClassCastException("Cannot cast $this to ${RootNode::class.simpleName}")

    override val isRuleNode: Boolean
        get() = false

    override fun asRuleNode(): RuleNode? = null

    override fun castToRuleNode(): RuleNode =
        asRuleNode() ?: throw ClassCastException("Cannot cast $this to ${RuleNode::class.simpleName}")

    override val isNoArgsNode: Boolean
        get() = false

    override fun asNoArgsNode(): NoArgsNode? = null

    override fun castToNoArgsNode(): NoArgsNode =
        asNoArgsNode() ?: throw ClassCastException("Cannot cast $this to ${NoArgsNode::class.simpleName}")

    /** Description for current Rete Tree Node */
    protected abstract val header: String

    override fun remove(
        element: E,
        limit: Int,
    ): Sequence<E> =
        when (limit) {
            0 -> emptySequence()
            else -> removeWithLimit(element, limit)
        }

    /** Called when a non-zero-limit removal is required inside a node */
    protected abstract fun removeWithLimit(
        element: E,
        limit: Int,
    ): Sequence<E>

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

    override fun toString(): String = toString(false)

    companion object {
        /** Utility function to deeply copy a MutableMap */
        @JvmStatic
        protected inline fun <K, V> MutableMap<K, V>.deepCopy(
            deepCopyKey: (K) -> K,
            deepCopyValue: (V) -> V,
        ): MutableMap<K, V> = entries.map { deepCopyKey(it.key) to deepCopyValue(it.value) }.toMap(mutableMapOf())
    }
}
