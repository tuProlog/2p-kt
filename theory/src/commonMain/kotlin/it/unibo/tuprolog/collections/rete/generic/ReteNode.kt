package it.unibo.tuprolog.collections.rete.generic

import it.unibo.tuprolog.collections.rete.generic.set.ArgNode
import it.unibo.tuprolog.collections.rete.generic.set.ArityNode
import it.unibo.tuprolog.collections.rete.generic.set.DirectiveNode
import it.unibo.tuprolog.collections.rete.generic.set.FunctorNode
import it.unibo.tuprolog.collections.rete.generic.set.NoArgsNode
import it.unibo.tuprolog.collections.rete.generic.set.RuleNode
import it.unibo.tuprolog.core.Clause
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.collections.rete.generic.set.RootNode as SetNode

/** A class modeling a Rete Tree Node */
internal interface ReteNode<K, E : Clause> {
    val isFunctorNode: Boolean

    val isArgNode: Boolean

    val isArityNode: Boolean

    val isDirectiveNode: Boolean

    val isRootNode: Boolean

    val isRuleNode: Boolean

    val isNoArgsNode: Boolean

    fun asFunctorNode(): FunctorNode?

    fun castToFunctorNode(): FunctorNode

    fun asArgNode(): ArgNode?

    fun castToArgNode(): ArgNode

    fun asArityNode(): ArityNode?

    fun castToArityNode(): ArityNode

    fun asDirectiveNode(): DirectiveNode?

    fun castToDirectiveNode(): DirectiveNode

    fun asRootNode(): SetNode?

    fun castToRootNode(): SetNode

    fun asRuleNode(): RuleNode?

    fun castToRuleNode(): RuleNode

    fun asNoArgsNode(): NoArgsNode?

    fun castToNoArgsNode(): NoArgsNode

    /** All direct children of this node */
    val children: MutableMap<K, ReteNode<*, E>>

    /** All indexed elements by this node and its children */
    val indexedElements: Sequence<E>

    /** Indexes a new element under this node */
    fun put(
        element: E,
        beforeOthers: Boolean,
    )

    /** Indexes a new element under this node, after the others */
    fun put(element: E) = put(element, false)

    /** Indexes all the element given as input, after the others**/
    fun put(clauses: Iterable<E>) =
        clauses.forEach {
            put(it)
        }

    /** Gets a sequence of matching elements starting from give one */
    fun get(element: @UnsafeVariance E): Sequence<E>

    /**
     * Removes elements matching given [element], respecting provided [limit], and returns them.
     * A negative limit will be considered as if there's no limit, i.e. all matching elements will be removed
     */
    fun remove(
        element: @UnsafeVariance E,
        limit: Int,
    ): Sequence<E>

    /**
     * Removes one element matching given [element], and returns it;
     */
    fun remove(element: @UnsafeVariance E): Sequence<E> = remove(element, 1)

    /** Removes all elements matching given one */
    fun removeAll(element: @UnsafeVariance E): Sequence<E>

    /** Creates a deep copy of the tree index; it doesn't copy indexed elements */
    fun deepCopy(): ReteNode<K, E>

    /** An enhanced toString that can print nodes in Tree fashion */
    fun toString(treefy: Boolean): String

    override fun toString(): String

    companion object {
        /** Creates a ReteTree from give clauses */
        @JvmStatic
        fun ofSet(clauses: Iterable<Clause>): ReteNode<*, Clause> = SetNode().apply { clauses.forEach { put(it) } }

        /** Creates a ReteTree from give clauses */
        @JvmStatic
        fun ofSet(vararg clauses: Clause): ReteNode<*, Clause> = ofSet(listOf(*clauses))
    }
}
