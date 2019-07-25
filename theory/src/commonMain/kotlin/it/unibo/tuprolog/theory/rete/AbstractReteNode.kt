package it.unibo.tuprolog.theory.rete

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.jvm.JvmStatic
import kotlin.math.min

/** Abstract base class for Rete Tree nodes */
internal abstract class AbstractReteNode<K, E>(override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf()) : ReteNode<K, E> {

    /** Description for current Rete Tree Node */
    protected abstract val header: String

    override val indexedElements: Sequence<E>
        get() = children.asSequence().flatMap { it.value.indexedElements }

    override fun removeAll(element: E): Sequence<E> = // TODO support direct removeAll in subclasses
            remove(element, Int.MAX_VALUE)

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {" +
                        children.values.joinToString(",\n\t", "\n\t", "\n") {
                            it.toString(treefy).replace("\n", "\n\t")
                        } + "}"
            else
                toString()

    override fun remove(element: E, limit: Int): Sequence<E> = when (limit) {
        0 -> emptySequence()
        else -> removeWithNonZeroLimit(element, limit)
    }

    /** Called when a non-zero-limit removal is required inside a node */
    protected abstract fun removeWithNonZeroLimit(element: E, limit: Int): Sequence<E>

    companion object {

        /** Utility function to deeply copy a MutableMap */
        @JvmStatic
        protected inline fun <K, V> MutableMap<K, V>.deepCopy(deepCopyKey: (K) -> K, deepCopyValue: (V) -> V): MutableMap<K, V> =
                entries.map { deepCopyKey(it.key) to deepCopyValue(it.value) }.toMap(mutableMapOf())
    }
}

/** A non-leaf Rete Node */
internal abstract class AbstractIntermediateNode<K, E>(override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf())
    : AbstractReteNode<K, E>(children) {

    /** Retrieves from receiver map those values of [ChildNodeType] that have a key respecting [keyFilter] */
    @Suppress("unused")
    protected inline fun <reified ChildNodeType> MutableMap<K, ReteNode<*, E>>.retrieve(keyFilter: (K) -> Boolean) =
            children.filterValues { node -> node is ChildNodeType }
                    .filterKeys(keyFilter)
                    .values.asSequence()

}

/** A leaf Rete Node */
internal abstract class AbstractLeafNode<E>(override val children: MutableMap<Nothing, ReteNode<*, E>> = mutableMapOf())
    : AbstractReteNode<Nothing, E>(children) {

    // TODO: 25/07/2019 refactor here common behaviour of leaf Directive And Rule Node
}

/** The root node, of the Rete Tree indexing [Clause]s */
internal data class RootNode(override val children: MutableMap<String?, ReteNode<*, Clause>> = mutableMapOf())
    : AbstractIntermediateNode<String?, Clause>(children) {

    override val header = "Root"

    override fun put(element: Clause, beforeOthers: Boolean) {
        when (element) {
            is Directive ->
                // safe cast because accessing children[null] is only for inserting directives
                @Suppress("UNCHECKED_CAST")
                children.getOrPut(null) { DirectiveNode() as ReteNode<*, Clause> }

            is Rule -> with(element.head.functor) {

                // safe cast because accessing children[functor] is only for inserting rules
                @Suppress("UNCHECKED_CAST")
                children.getOrPut(this) { FunctorNode(this) as ReteNode<*, Clause> }

            }

            else -> null

        }?.put(element, beforeOthers)
    }

    override fun get(element: Clause): Sequence<Clause> =
            when (element) {
                is Directive -> children[null]?.get(element)
                is Rule -> children[element.head.functor]?.get(element)
                else -> null
            } ?: emptySequence()

    override fun removeWithNonZeroLimit(element: Clause, limit: Int): Sequence<Clause> =
            when (element) {
                is Directive -> children[null]
                is Rule -> children[element.head.functor]
                else -> null
            }?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): RootNode = RootNode(children.deepCopy({ it }, { it.deepCopy() }))
}

/** An intermediate node indexing by Rules head's functor */
internal data class FunctorNode(private val functor: String, override val children: MutableMap<Int, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractIntermediateNode<Int, Rule>(children) {

    override val header = "Functor($functor)"

    override fun put(element: Rule, beforeOthers: Boolean) {
        if (functor == element.head.functor) with(element.head.arity) {

            children.getOrPut(this) { ArityNode(this) }
                    .put(element, beforeOthers)
        }
    }

    override fun get(element: Rule): Sequence<Rule> =
            children[element.head.arity]?.get(element) ?: emptySequence()

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            children[element.head.arity]
                    ?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): FunctorNode = FunctorNode(functor, children.deepCopy({ it }, { it.deepCopy() }))
}

/** An intermediate node indexing by Rules head's arity */
internal data class ArityNode(private val arity: Int, override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractIntermediateNode<Term?, Rule>(children) {

    init {
        require(arity >= 0) { "ArityNode arity should be greater than or equal to 0" }
    }

    override val header = "Arity($arity)"

    override fun put(element: Rule, beforeOthers: Boolean) = when {
        element.head.arity > 0 -> {
            val headFirstArg = element.head[0]
            val child = children.getOrElse(headFirstArg) {

                children.retrieve<ArgNode> { head -> head != null && head structurallyEquals headFirstArg }
                        .singleOrNull()
            }

            child ?: ArgNode(0, headFirstArg).also { children[headFirstArg] = it }
        }

        else -> children.getOrPut(null) { NoArgsNode() }

    }.put(element, beforeOthers)


    override fun get(element: Rule): Sequence<Rule> = when {
        element.head.arity > 0 -> {
            val headFirstArg: Term = element.head[0]

            children.retrieve<ArgNode> { head -> head != null && head matches headFirstArg }
                    .flatMap { node -> node.get(element) }
        }
        else -> children[null]?.get(element) ?: emptySequence()
    }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> = when {
        element.head.arity > 0 -> {
            val headFirstArg: Term = element.head[0]

            val removed: MutableList<Rule> = mutableListOf()
            children.retrieve<ArgNode> { head -> head != null && head matches headFirstArg }
                    .forEach { child ->
                        removed += child.remove(element, limit - removed.size)
                        if (removed.size == limit) return@forEach
                    }

            removed.asSequence()
        }
        else -> children[null]?.remove(element, limit) ?: emptySequence()
    }

    override fun deepCopy(): ArityNode = ArityNode(arity, children.deepCopy({ it }, { it.deepCopy() }))
}

/** An intermediate node indexing Rules with no-args' heads */
internal data class NoArgsNode(override val children: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractIntermediateNode<Nothing?, Rule>(children) {

    override val header = "NoArguments"

    override fun put(element: Rule, beforeOthers: Boolean) =
            children.getOrPut(null) { RuleNode() }
                    .put(element, beforeOthers)

    override fun get(element: Rule): Sequence<Rule> =
            children[null]?.get(element) ?: emptySequence()

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            children[null]?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): NoArgsNode = NoArgsNode(children.deepCopy({ it }, { it.deepCopy() }))
}

/** The arg node indexes Clauses by argument, starting from first to all the others */
internal data class ArgNode(private val index: Int, private val term: Term, override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractIntermediateNode<Term?, Rule>(children) {

    init {
        require(index >= 0) { "ArgNode index should be greater than or equal to 0" }
    }

    override val header = "Argument($index, $term)"

    override fun put(element: Rule, beforeOthers: Boolean) = when {
        index < element.head.arity - 1 -> { // if more arguments after "index" arg
            val nextArg: Term = element.head[index + 1]

            val child = children.getOrElse(nextArg) {
                children.retrieve<ArgNode> { head -> head != null && head structurallyEquals nextArg }
                        .singleOrNull()
            }

            child ?: ArgNode(index + 1, nextArg).also { children[nextArg] = it }
        }

        else -> children.getOrPut(null) { RuleNode() }

    }.put(element, beforeOthers)


    override fun get(element: Rule): Sequence<Rule> = when {
        index < element.head.arity - 1 -> {
            val nextArg: Term = element.head[index + 1]

            children.retrieve<ArgNode> { head -> head != null && head matches nextArg }
                    .flatMap { it.get(element) }
        }
        else -> children[null]?.get(element) ?: emptySequence()
    }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> = when {
        index < element.head.arity - 1 -> {
            val nextArg: Term = element.head[index + 1]

            val removed: MutableList<Rule> = mutableListOf()
            children.retrieve<ArgNode> { head -> head != null && head matches nextArg }
                    .forEach { child ->
                        removed += child.remove(element, limit - removed.size)
                        if (removed.size == limit) return@forEach
                    }

            removed.asSequence()
        }
        else -> children[null]?.remove(element, limit) ?: emptySequence()
    }

    override fun deepCopy(): ArgNode = ArgNode(index, term, children.deepCopy({ it }, { it.deepCopy() }))
}


/** A leaf node containing [Directive]s */
internal data class DirectiveNode(private val directives: MutableList<Directive> = mutableListOf())
    : AbstractLeafNode<Directive>() {

    override val header = "Directives"

    override val indexedElements: Sequence<Directive>
        get() = directives.asSequence()

    override fun put(element: Directive, beforeOthers: Boolean) {
        if (beforeOthers)
            directives.add(0, element)
        else
            directives.add(element)
    }

    override fun get(element: Directive): Sequence<Directive> =
            indexedElements.filter { it matches element }

    override fun removeWithNonZeroLimit(element: Directive, limit: Int): Sequence<Directive> =
            directives
                    .filter { it matches element }
                    .take(if (limit > 0) min(limit, directives.size) else directives.size)
                    .toList().asSequence()
                    .also { directives.removeAll(it) }

    override fun deepCopy(): DirectiveNode = DirectiveNode(directives.toMutableList())

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {${directives.joinToString(".\n\t", "\n\t", ".\n")}}"
            else
                toString()
}

/** A leaf node containing [Rule]s */
internal data class RuleNode(private val rules: MutableList<Rule> = mutableListOf()) : AbstractLeafNode<Rule>() {

    override val header = "Rules"

    override val indexedElements: Sequence<Rule>
        get() = rules.asSequence()

    override fun put(element: Rule, beforeOthers: Boolean) {
        if (beforeOthers)
            rules.add(0, element)
        else
            rules.add(element)
    }

    override fun get(element: Rule): Sequence<Rule> = indexedElements.filter { it matches element }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            rules.filter { it matches element }
                    .take(if (limit > 0) min(limit, rules.size) else rules.size)
                    .toList().asSequence()
                    .also { rules.removeAll(it) }


    override fun deepCopy(): RuleNode = RuleNode(rules.toMutableList())

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {${rules.joinToString(".\n\t", "\n\t", ".\n")}}"
            else
                toString()
}
