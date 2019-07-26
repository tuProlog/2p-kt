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

    override fun removeAll(element: E): Sequence<E> =
            remove(element, Int.MAX_VALUE)

    override fun remove(element: E, limit: Int): Sequence<E> = when (limit) {
        0 -> emptySequence()
        else -> removeWithNonZeroLimit(element, limit)
    }

    /** Called when a non-zero-limit removal is required inside a node */
    protected abstract fun removeWithNonZeroLimit(element: E, limit: Int): Sequence<E>

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {" +
                        children.values.joinToString(",\n\t", "\n\t", "\n") {
                            it.toString(treefy).replace("\n", "\n\t")
                        } + "}"
            else
                toString()

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

    /** Selects correct children, according to [element], onto which to propagate received method calls */
    protected abstract fun selectChildren(element: E): Sequence<ReteNode<*, E>?>

    override fun get(element: E): Sequence<E> =
            selectChildren(element).flatMap { it?.get(element) ?: emptySequence() }

    override fun removeAll(element: E): Sequence<E> =
            selectChildren(element).toList().flatMap {
                it?.removeAll(element)?.toList() ?: emptyList()
            }.asSequence()

    /** Retrieves from receiver map those values of [ChildNodeType] that have a key respecting [keyFilter] */
    @Suppress("unused")
    protected inline fun <reified ChildNodeType> MutableMap<K, ReteNode<*, E>>.retrieve(keyFilter: (K) -> Boolean) =
            filterValues { node -> node is ChildNodeType }
                    .filterKeys(keyFilter)
                    .values.asSequence()

    /**
     *  A [Sequence.fold] function that stops when accumulated operation results' count becomes greater or equal to [limit];
     *
     * if negative [limit] this behaves actually like normal [Sequence.fold]
     */
    protected inline fun <T, R : Iterable<*>> Sequence<T>.foldWithLimit(initial: R, limit: Int, operation: (acc: R, T) -> R) =
            fold(initial) { accumulator, element ->
                if (limit < 0 || accumulator.count() < limit)
                    operation(accumulator, element)
                else
                    return@foldWithLimit accumulator
            }
}

/** A leaf Rete Node */
internal abstract class AbstractLeafNode<E>(override val children: MutableMap<Nothing, ReteNode<*, E>> = mutableMapOf())
    : AbstractReteNode<Nothing, E>(children) {

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

    override fun removeWithNonZeroLimit(element: E, limit: Int): Sequence<E> =
            get(element)
                    .take(if (limit > 0) min(limit, leafElements.count()) else leafElements.count())
                    .toList().asSequence() // toList needed to reify the get's returned sequence, and not evaluate it two times
                    .also { leafElements.removeAll(it) }

    override fun removeAll(element: E): Sequence<E> =
            get(element).toList().asSequence() // toList needed to reify the get's returned sequence, and not evaluate it two times
                    .also { leafElements.removeAll(it) }

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {${leafElements.joinToString(".\n\t", "\n\t", ".\n")}}"
            else
                toString()
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

    override fun selectChildren(element: Clause) = sequenceOf(
            when (element) {
                is Directive -> children[null]
                is Rule -> children[element.head.functor]
                else -> null
            }
    )

    override fun removeWithNonZeroLimit(element: Clause, limit: Int): Sequence<Clause> =
            selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

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

    override fun selectChildren(element: Rule) = sequenceOf(children[element.head.arity])

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

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

    override fun selectChildren(element: Rule) = when {
        element.head.arity > 0 ->
            children.retrieve<ArgNode> { head -> head != null && head matches element.head[0] }

        else -> sequenceOf(children[null])
    }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            selectChildren(element)
                    .foldWithLimit(mutableListOf<Rule>(), limit) { yetRemoved, currentChild ->
                        yetRemoved.also {
                            it += currentChild?.remove(element, limit - it.count())
                                    ?: emptySequence()
                        }
                    }.asSequence()

    // removeAll optimized implementation w.r.t. super class
    override fun removeAll(element: Rule): Sequence<Rule> =
            selectChildren(element)
                    .fold(mutableListOf<Rule>()) { yetRemoved, currentChild ->
                        yetRemoved.also {
                            it += currentChild?.removeAll(element) ?: emptySequence()
                        }
                    }.asSequence()

    override fun deepCopy(): ArityNode = ArityNode(arity, children.deepCopy({ it }, { it.deepCopy() }))
}

/** An intermediate node indexing Rules with no-args' heads */
internal data class NoArgsNode(override val children: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractIntermediateNode<Nothing?, Rule>(children) {

    override val header = "NoArguments"

    override fun put(element: Rule, beforeOthers: Boolean) =
            children.getOrPut(null) { RuleNode() }
                    .put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> =
            sequenceOf(children[null])

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

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
            val nextArg = element.head[index + 1]

            val child = children.getOrElse(nextArg) {
                children.retrieve<ArgNode> { head -> head != null && head structurallyEquals nextArg }
                        .singleOrNull()
            }

            child ?: ArgNode(index + 1, nextArg).also { children[nextArg] = it }
        }

        else -> children.getOrPut(null) { RuleNode() }

    }.put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> = when {
        index < element.head.arity - 1 -> {
            val nextArg = element.head[index + 1]
            children.retrieve<ArgNode> { head -> head != null && head matches nextArg }
        }
        else -> sequenceOf(children[null])
    }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
            selectChildren(element)
                    .foldWithLimit(mutableListOf<Rule>(), limit) { yetRemoved, currentChild ->
                        yetRemoved.also {
                            it += currentChild?.remove(element, limit - it.count())
                                    ?: emptySequence()
                        }
                    }.asSequence()

    // removeAll optimized implementation w.r.t. super class
    override fun removeAll(element: Rule): Sequence<Rule> =
            selectChildren(element)
                    .fold(mutableListOf<Rule>()) { yetRemoved, currentChild ->
                        yetRemoved.also {
                            it += currentChild?.removeAll(element) ?: emptySequence()
                        }
                    }.asSequence()

    override fun deepCopy(): ArgNode = ArgNode(index, term, children.deepCopy({ it }, { it.deepCopy() }))
}


/** A leaf node containing [Directive]s */
internal data class DirectiveNode(override val leafElements: MutableList<Directive> = mutableListOf())
    : AbstractLeafNode<Directive>() {

    override val header = "Directives"

    override fun get(element: Directive): Sequence<Directive> = indexedElements.filter { it matches element }

    override fun deepCopy(): DirectiveNode = DirectiveNode(leafElements.toMutableList())
}

/** A leaf node containing [Rule]s */
internal data class RuleNode(override val leafElements: MutableList<Rule> = mutableListOf()) : AbstractLeafNode<Rule>() {

    override val header = "Rules"

    override fun get(element: Rule): Sequence<Rule> = indexedElements.filter { it matches element }

    override fun deepCopy(): RuleNode = RuleNode(leafElements.toMutableList())
}
