package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unifier.Companion.matches

sealed class ReteTree(open val children: MutableList<ReteTree>) {

    data class RootNode(override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "Root"

        override fun canContain(clause: Clause): Boolean {
            return true
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Directive -> {
                    var child = children.find { it.canContain(clause) }

                    if (child === null) {
                        child = DirectiveNode(mutableListOf())
                        children.add(child)
                    }

                    child.put(clause)
                }
                is Rule -> {
                    var child = children.find { it.canContain(clause) }

                    if (child === null) {
                        child = FunctorNode(clause.head.functor, mutableListOf())
                        children.add(child)
                    }
                    child.put(clause)
                }
            }
        }

        override fun clone(): RootNode {
            return RootNode(children.map { it.clone() }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Directive -> children.asSequence().filterIsInstance<DirectiveNode>().flatMap { it.get(clause) }
                else -> children.asSequence().filterIsInstance<FunctorNode>().flatMap { it.get(clause) }
            }
        }
    }

    data class DirectiveNode(val directives: MutableList<Directive>) : ReteTree(mutableListOf()) {

        override val header: String
            get() = "Directives"

        override val clauses: Sequence<Clause>
            get() = directives.asSequence()

        override fun canContain(clause: Clause): Boolean {
            return clause is Directive
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Directive -> directives.add(clause)
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

    data class FunctorNode(val functor: String, override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "Functor($functor)"

        override fun canContain(clause: Clause): Boolean {
            return clause is Rule && functor == clause.head.functor
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canContain(clause) }

                    if (child === null) {
                        child = ArityNode(clause.head.arity, mutableListOf())
                        children.add(child)
                    }
                    child.put(clause)
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

    data class ArityNode(val arity: Int, override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "Arity($arity)"

        override fun canContain(clause: Clause): Boolean {
            return clause is Rule && arity == clause.head.arity
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canContain(clause) }

                    if (child === null) {
                        child = ArgNode(0, clause.head[0], mutableListOf())
                        children.add(child)
                    }
                    child.put(clause)
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

    data class ArgNode(val index: Int, val term: Term, override val children: MutableList<ReteTree>) : ReteTree(children) {

        override val header: String
            get() = "Argument($index, $term)"

        override fun canContain(clause: Clause): Boolean {
            return term structurallyEquals clause.head!![index]
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Rule -> {
                    var child = children.find { it.canContain(clause) }

                    if (child === null) {
                        if (index < clause.head.arity - 1) {
                            child = ArgNode(index + 1, clause.head[index + 1], mutableListOf())
                        } else {
                            child = RuleNode(mutableListOf())
                        }
                        children.add(child)
                    }
                    child.put(clause)
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

        override fun canContain(clause: Clause): Boolean {
            return true
        }

        override fun put(clause: Clause) {
            when (clause) {
                is Rule -> rules.add(clause)
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

    fun <T : ReteTree> getAs(i: Int): T {
        return children[i] as T
    }

    val size: Int
        get() = children.size

    abstract fun clone(): ReteTree

    internal abstract fun put(clause: Clause)

    protected abstract fun canContain(clause: Clause): Boolean

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
        fun of(clauses: List<Clause>): ReteTree {
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
