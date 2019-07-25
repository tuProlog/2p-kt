package it.unibo.tuprolog.theory.rete

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.jvm.JvmStatic
import kotlin.math.min

/** Abstract base class for rete tree nodes */
internal abstract class AbstractReteNode<K, E>(override val children: MutableMap<K, ReteNode<*, E>> = mutableMapOf()) : ReteNode<K, E> {

    /** Description for current Rete Tree Node */
    protected abstract val header: String

    override val indexedElements: Sequence<E>
        get() = children.asSequence().flatMap { it.value.indexedElements }

    override fun removeAll(clause: E): Sequence<E> = // TODO support direct removeAll in subclasses
            remove(clause, Int.MAX_VALUE)

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
        protected fun <K, V> MutableMap<K, V>.deepCopy(deepCopyKey: (K) -> K, deepCopyValue: (V) -> V): MutableMap<K, V> =
                entries.map { deepCopyKey(it.key) to deepCopyValue(it.value) }.toMap(mutableMapOf())
    }
}

/** The root node, of the Rete Tree indexing [Clause]s */
internal data class RootNode(override val children: MutableMap<String?, ReteNode<*, Clause>> = mutableMapOf())
    : AbstractReteNode<String?, Clause>(children) {

    override val header = "Root"

    override fun put(element: Clause, beforeOthers: Boolean) {
        when (element) {
            is Directive -> {
                var child: DirectiveNode? = children[null] as DirectiveNode?

                if (child === null) {
                    child = DirectiveNode()

                    // safe cast because accessing children[null] is only for inserting directives
                    @Suppress("UNCHECKED_CAST")
                    children[null] = child as ReteNode<*, Clause>
                }

                child.put(element, beforeOthers)
            }
            is Rule -> {
                val functor: String = element.head.functor
                var child: FunctorNode? = children[functor] as FunctorNode?

                if (child === null) {
                    child = FunctorNode(functor)

                    // safe cast because accessing children[functor] is only for inserting rules
                    @Suppress("UNCHECKED_CAST")
                    children[functor] = child as ReteNode<*, Clause>
                }
                child.put(element, beforeOthers)
            }
        }
    }

    override fun get(element: Clause): Sequence<Clause> =
            when (element) {
                is Directive -> {
                    children[null]?.get(element) ?: emptySequence()
                }
                is Rule -> {
                    children[element.head.functor]?.get(element) ?: emptySequence()
                }
                else -> emptySequence()
            }

    override fun remove(element: Clause, limit: Int): Sequence<Clause> {
        if (limit == 0) {
            return emptySequence()
        }

        val child: ReteNode<*, Clause>? = when (element) {
            is Directive -> {
                children[null]
            }
            is Rule -> {
                children[element.head.functor]
            }
            else -> throw IllegalStateException()
        }

        return child?.remove(element, limit) ?: emptySequence()
    }

    override fun deepCopy(): RootNode = RootNode(children.deepCopy({ it }, { it.deepCopy() }))
}

internal data class DirectiveNode(private val directives: MutableList<Directive> = mutableListOf())
    : AbstractReteNode<Nothing, Directive>() {

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

    override fun remove(element: Directive, limit: Int): Sequence<Directive> {
        if (limit == 0) {
            return emptySequence()
        }

        val toTake = if (limit > 0) min(limit, directives.size) else directives.size
        val result = mutableListOf<Directive>()
        val i = directives.iterator()
        var j = 0
        while (i.hasNext() && j < toTake) {
            with(i.next()) {
                if (this.matches(element)) {
                    result.add(this)
                    i.remove()
                    j++
                }
            }
        }
        return result.asSequence()
    }

    override fun deepCopy(): DirectiveNode = DirectiveNode(directives.toMutableList())

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {" +
                        directives.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            else
                toString()

}

internal data class FunctorNode(private val functor: String, override val children: MutableMap<Int, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractReteNode<Int, Rule>(children) {

    override val header = "Functor($functor)"

    override fun put(element: Rule, beforeOthers: Boolean) {
        if (functor == element.head.functor) {
            val arity: Int = element.head.arity
            var child: ArityNode? = children[arity] as ArityNode?

            if (child === null) {
                child = ArityNode(arity)
                children[arity] = child
            }
            child.put(element, beforeOthers)
        }
    }

    override fun get(element: Rule): Sequence<Rule> =
            children[element.head.arity]?.get(element) ?: emptySequence()

    override fun remove(element: Rule, limit: Int): Sequence<Rule> =
            when (limit) {
                0 -> emptySequence()

                else -> children[element.head.arity]?.remove(element, limit) ?: emptySequence()
            }

    override fun deepCopy(): FunctorNode = FunctorNode(functor, children.deepCopy({ it }, { it.deepCopy() }))
}

internal data class ArityNode(private val arity: Int, override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractReteNode<Term?, Rule>(children) {

    init {
        require(arity >= 0) { "ArityNode arity should be greater than or equal to 0" }
    }

    override val header = "Arity($arity)"

    override fun put(element: Rule, beforeOthers: Boolean) {
        when {
            element.head.arity > 0 -> {
                val firstArg: Term = element.head[0]
                var child: ArgNode? = children[firstArg] as ArgNode?

                if (child === null) {
                    child = children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .find { it.key!! structurallyEquals firstArg }
                            ?.value as ArgNode?
                }

                if (child === null) {
                    child = ArgNode(0, firstArg)
                    children[firstArg] = child
                }

                child.put(element, beforeOthers)
            }
            else -> {
                var child: NoArgsNode? = children[null] as NoArgsNode?

                if (child === null) {
                    child = NoArgsNode()
                    children[null] = child
                }

                child.put(element, beforeOthers)
            }
        }
    }

    override fun get(element: Rule): Sequence<Rule> =
            when {
                element.head.arity > 0 -> {
                    val firstArg: Term = element.head[0]

                    children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(firstArg) }
                            .map { it.value }
                            .flatMap { it.get(element) }
                }
                else -> children[null]?.get(element) ?: emptySequence()
            }

    override fun remove(element: Rule, limit: Int): Sequence<Rule> =
            when {
                limit == 0 -> emptySequence()

                element.head.arity > 0 -> {
                    val firstArg: Term = element.head[0]

                    val removed: MutableList<Rule> = mutableListOf()
                    for (child in children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(firstArg) }
                            .map { it.value }) {

                        removed += child.remove(element, limit - removed.size)
                        if (removed.size == limit) break
                    }

                    removed.asSequence()
                }
                else -> children[null]?.remove(element, limit) ?: emptySequence()
            }

    override fun deepCopy(): ArityNode = ArityNode(arity, children.deepCopy({ it }, { it.deepCopy() }))
}

internal data class NoArgsNode(override val children: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractReteNode<Nothing?, Rule>(children) {

    override val header = "NoArguments"

    override fun put(element: Rule, beforeOthers: Boolean) {
        var child: RuleNode? = children[null] as RuleNode?

        if (child == null) {
            child = RuleNode()
            children[null] = child
        }

        child.put(element, beforeOthers)
    }

    override fun get(element: Rule): Sequence<Rule> =
            children[null]?.get(element) ?: emptySequence()

    override fun remove(element: Rule, limit: Int): Sequence<Rule> =
            when {
                limit == 0 || children.isEmpty() -> emptySequence()

                else -> children[null]?.remove(element, limit) ?: emptySequence()
            }

    override fun deepCopy(): NoArgsNode = NoArgsNode(children.deepCopy({ it }, { it.deepCopy() }))
}

/** The arg node indexes Clauses by argument, starting from first to all the others */
internal data class ArgNode(private val index: Int, private val term: Term, override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf())
    : AbstractReteNode<Term?, Rule>(children) {

    init {
        require(index >= 0) { "ArgNode index should be greater than or equal to 0" }
    }

    override val header = "Argument($index, $term)"

    override fun put(element: Rule, beforeOthers: Boolean) =
            when {
                index < element.head.arity - 1 -> { // if more arguments after "index" arg
                    val nextArg: Term = element.head[index + 1]
                    var child: ArgNode? = children[nextArg] as ArgNode?

                    if (child === null) {
                        child = children.entries.asSequence()
                                .filter { it.key !== null }
                                .filter { it.value is ArgNode }
                                .find { it.key!! structurallyEquals nextArg }
                                ?.value as ArgNode?
                    }

                    if (child === null) {
                        child = ArgNode(index + 1, nextArg)
                        children[nextArg] = child
                    }

                    (child as ReteNode<*, Rule>).put(element, beforeOthers)
                }
                else -> {
                    var child: RuleNode? = children[null] as RuleNode?

                    if (child === null) {
                        child = RuleNode()
                        children[null] = child
                    }

                    child.put(element, beforeOthers)
                }
            }


    override fun get(element: Rule): Sequence<Rule> =
            when {
                index < element.head.arity - 1 -> {
                    val nextArg: Term = element.head[index + 1]

                    children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }
                            .flatMap { it.get(element) }
                }
                else -> children[null]?.get(element) ?: emptySequence()
            }

    override fun remove(element: Rule, limit: Int): Sequence<Rule> =
            when {
                limit == 0 -> emptySequence()

                index < element.head.arity - 1 -> {
                    val nextArg: Term = element.head[index + 1]

                    val removed: MutableList<Rule> = mutableListOf()
                    for (child in children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }) {

                        removed += child.remove(element, limit - removed.size)
                        if (removed.size == limit) break
                    }

                    removed.asSequence()
                }
                else -> children[null]?.remove(element, limit) ?: emptySequence()
            }

    override fun deepCopy(): ArgNode = ArgNode(index, term, children.deepCopy({ it }, { it.deepCopy() }))
}


internal data class RuleNode(private val rules: MutableList<Rule> = mutableListOf()) : AbstractReteNode<Nothing, Rule>() {

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

    override fun remove(element: Rule, limit: Int): Sequence<Rule> {
        if (limit == 0) {
            return emptySequence()
        }

        val toTake = if (limit > 0) min(limit, rules.size) else rules.size
        val result = mutableListOf<Rule>()
        val i = rules.iterator()
        var j = 0
        while (i.hasNext() && j < toTake) {
            with(i.next()) {
                if (this.matches(element)) {
                    result.add(this)
                    i.remove()
                    j++
                }
            }
        }
        return result.asSequence()
    }

    override fun deepCopy(): RuleNode = RuleNode(rules.toMutableList())

    override fun toString(treefy: Boolean): String =
            if (treefy)
                "$header {" +
                        rules.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            else
                toString()
}
