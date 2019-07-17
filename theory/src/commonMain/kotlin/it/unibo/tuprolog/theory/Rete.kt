package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.math.min

sealed class ReteTree<K>(open val children: MutableMap<K, out ReteTree<*>>) {

    data class RootNode(override val children: MutableMap<String?, ReteTree<*>>) : ReteTree<String?>(children) {
        override val header: String
            get() = "Root"

        override fun canPut(clause: Clause): Boolean {
            return true
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val child: ReteTree<*>? = when (clause) {
                is Directive -> {
                    children[null]
                }
                is Rule -> {
                    children[clause.head.functor]
                }
                else -> throw IllegalArgumentException()
            }

            return child?.remove(clause, limit) ?: emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> {
                    var child: DirectiveNode? = children[null] as DirectiveNode?

                    if (child === null) {
                        child = DirectiveNode(mutableListOf())
                        children[null] = child
                    }

                    child.put(clause, before)
                }
                is Rule -> {
                    val functor: String = clause.head.functor
                    var child: FunctorNode? = children[functor] as FunctorNode?

                    if (child === null) {
                        child = FunctorNode(clause.head.functor, mutableListOf())
                        children[functor] = child
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): RootNode {
            return RootNode(mutableMapOf(*children.map { it.key to it.value }.toList().toTypedArray()))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Directive -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
                is Rule -> {
                    children[clause.head.functor]?.get(clause) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }
    }

    data class DirectiveNode(val directives: MutableList<Directive>) : ReteTree<Nothing>(mutableMapOf()) {

        override val header: String
            get() = "Directives"

        override val clauses: Sequence<Clause>
            get() = directives.asSequence()

        override fun canPut(clause: Clause): Boolean {
            return clause is Directive
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> if (before) directives.add(0, clause) else directives.add(clause)
            }
        }

        override fun clone(): DirectiveNode {
            return DirectiveNode(directives.map { it } .toMutableList())
        }

        override fun toString(treefy: Boolean): String {
            return if (treefy) {
                "$header {" +
                        directives.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            } else {
                toString()
            }
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Directive -> directives.asSequence().filter { it matches clause }
                else -> emptySequence()
            }
        }

    }

    data class FunctorNode(val functor: String, override val children: MutableMap<Int, ArityNode>) : ReteTree<Int>(children) {

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            return children.find { it.canRemove(clause) } ?.remove(clause, limit) ?: emptySequence()
        }

        override val header: String
            get() = "Functor($functor)"

        override fun canPut(clause: Clause): Boolean {
            return clause is Rule && functor == clause.head.functor
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canPut(clause) }

                    if (child === null) {
                        child = ArityNode(clause.head.arity, mutableListOf())
                        children.add(child)
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): FunctorNode {
            return FunctorNode(functor, children.map { it.clone() }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause is Rule && functor == clause.head.functor -> {
                    children.asSequence().filterIsInstance<ArityNode>().flatMap { it.get(clause) }
                }
                else -> emptySequence()
            }
        }
    }

    data class ArityNode(val arity: Int, override val children: MutableMap<Term, ReteTree<*>>) : ReteTree<Term>(children) {

        override val header: String
            get() = "Arity($arity)"

        override fun canPut(clause: Clause): Boolean {
            return clause is Rule && arity == clause.head.arity
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            return children.find { it.canRemove(clause) } ?.remove(clause, limit) ?: emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canPut(clause) }

                    if (child === null) {
                        if (clause.head.arity > 0) {
                            child = ArgNode(0, clause.head[0], mutableListOf())
                            children.add(child)
                        } else {
                            child = NoArgsNode(mutableListOf())
                            children.add(child)
                        }
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): ArityNode {
            return ArityNode(arity, children.map { it.clone() }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause is Rule && arity == clause.head.arity -> {
                    children.asSequence().filterIsInstance<ArgNode>().flatMap { it.get(clause) }
                }
                else -> emptySequence()
            }
        }
    }

    data class NoArgsNode(override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "NoArguments"

        override fun canPut(clause: Clause): Boolean {
            return clause is Rule && clause.head.arity == 0
        }

        override fun canRemove(clause: Clause): Boolean {
            return canPut(clause)
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            return children.find { it.canRemove(clause) }?.remove(clause, limit) ?: emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canPut(clause) }

                    if (child === null) {
                        child = RuleNode(mutableListOf())
                        children.add(child)
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): NoArgsNode {
            return NoArgsNode(children.map { it.clone() }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause is Rule && clause.head.arity == 0 -> {
                    children.asSequence().filterIsInstance<RuleNode>().flatMap { it.get(clause) }
                }
                else -> emptySequence()
            }
        }
    }

    data class ArgNode(val index: Int, val term: Term, override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "Argument($index, $term)"

        override fun canPut(clause: Clause): Boolean {
            return clause is Rule && term structurallyEquals clause.head[index]
        }

        override fun canRemove(clause: Clause): Boolean {
            return term matches clause.head!![index]
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            return children.find { it.canRemove(clause) } ?.remove(clause, limit) ?: emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canPut(clause) }

                    if (child === null) {
                        child = if (index < clause.head.arity - 1) {
                            ArgNode(index + 1, clause.head[index + 1], mutableListOf())
                        } else {
                            RuleNode(mutableListOf())
                        }
                        children.add(child)
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): ArgNode {
            return ArgNode(index, term, children.map { it.clone() }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause is Rule && term structurallyEquals clause.head[index] -> {
                    if (index == clause.head.arity - 1) {
                        children.asSequence().filterIsInstance<RuleNode>().flatMap { it.get(clause) }
                    } else {
                        children.asSequence().filterIsInstance<ArgNode>().flatMap { it.get(clause) }
                    }
                }
                else -> emptySequence()
            }
        }
    }

    data class RuleNode(val rules: MutableList<Rule>) : ReteTree(mutableListOf()) {

        override val header: String
            get() = "Rules"

        override val clauses: Sequence<Clause>
            get() = rules.asSequence()

        override fun canPut(clause: Clause): Boolean {
            return true
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val toTake = if (limit > 0) min(limit, rules.size) else rules.size
            val result = mutableListOf<Clause>()
            val i = rules.iterator()
            var j = 0
            while (i.hasNext() && j < toTake) {
                with(i.next()) {
                    if (this.matches(clause)) {
                        result.add(this)
                        i.remove()
                        j++
                    }
                }
            }
            return result.asSequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> if (before) rules.add(0, clause) else rules.add(clause)
            }
        }

        override fun clone(): RuleNode {
            return RuleNode(rules.map { it } .toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return rules.asSequence().filter { it matches clause }
        }

        override fun toString(treefy: Boolean): String {
            return if (treefy) {
                "$header {" +
                        rules.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            } else {
                toString()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ReteTree> getAs(i: Int): T {
        return children[i] as T
    }

    val size: Int
        get() = children.size

    abstract fun clone(): ReteTree

    internal abstract fun put(clause: Clause, before: Boolean = false)

    internal abstract fun remove(clause: Clause, limit: Int = 1): Sequence<Clause>

    internal fun removeAll(clause: Clause): Sequence<Clause> {
        return remove(clause, Int.MAX_VALUE)
    }

    protected abstract fun canPut(clause: Clause): Boolean

    protected open fun canRemove(clause: Clause): Boolean {
        return canPut(clause)
    }

    abstract fun get(clause: Clause): Sequence<Clause>

    open fun toString(treefy: Boolean): String {
        return if (treefy) {
            "$header {" +
                    children.joinToString(",\n\t", "\n\t", "\n") {
                        it.toString(treefy).replace("\n", "\n\t")
                    } +
                    "}"
        } else {
            toString()
        }
    }

    open val clauses: Sequence<Clause>
        get() = children.asSequence().flatMap { it.clauses }


    protected abstract val header: String

    companion object {
        fun of(clauses: Iterable<Clause>): ReteTree {
            return RootNode(mutableListOf()).apply {
                for (clause in clauses) {
                    put(clause)
                }
            }
        }

        fun of(vararg clauses: Clause): ReteTree {
            return of(listOf(*clauses))
        }
    }
}
