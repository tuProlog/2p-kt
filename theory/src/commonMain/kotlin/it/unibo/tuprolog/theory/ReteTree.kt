package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.math.min

internal sealed class ReteTree<K>(open val children: MutableMap<K, out ReteTree<*>> = mutableMapOf()) {

    data class RootNode(override val children: MutableMap<String?, ReteTree<*>> = mutableMapOf())
        : ReteTree<String?>(children) {
        override val header = "Root"

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> {
                    var child: DirectiveNode? = children[null] as DirectiveNode?

                    if (child === null) {
                        child = DirectiveNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
                is Rule -> {
                    val functor: String = clause.head.functor
                    var child: FunctorNode? = children[functor] as FunctorNode?

                    if (child === null) {
                        child = FunctorNode(functor)
                        children[functor] = child
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
                when (clause) {
                    is Directive -> {
                        children[null]?.get(clause) ?: emptySequence()
                    }
                    is Rule -> {
                        children[clause.head.functor]?.get(clause) ?: emptySequence()
                    }
                    else -> emptySequence()
                }

        override fun get(functor: String, arity: Int): Sequence<Clause> {
            return children[functor]?.get(functor, arity) ?: emptySequence()
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
                else -> throw IllegalStateException()
            }

            return child?.remove(clause, limit) ?: emptySequence()
        }

        override fun deepCopy(): RootNode = RootNode(children.deepCopy({ it }, { it.deepCopy() }))
    }

    data class DirectiveNode(private val directives: MutableList<Directive> = mutableListOf())
        : ReteTree<Nothing>() {

        override val header = "Directives"

        override val clauses: Sequence<Clause>
            get() = directives.asSequence()

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> if (before) directives.add(0, clause) else directives.add(clause)
            }
        }

        override fun get(clause: Clause): Sequence<Clause> = when (clause) {
            is Directive -> clauses.filter { it matches clause }
            else -> emptySequence() // only performance purposes type-check
        }

        override fun get(functor: String, arity: Int): Sequence<Clause> = emptySequence()

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val toTake = if (limit > 0) min(limit, directives.size) else directives.size
            val result = mutableListOf<Clause>()
            val i = directives.iterator()
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

        override fun deepCopy(): DirectiveNode = DirectiveNode(directives.toMutableList())

        override fun toString(treefy: Boolean): String {
            return if (treefy) {
                "$header {" +
                        directives.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            } else {
                toString()
            }
        }

    }

    data class FunctorNode(private val functor: String, override val children: MutableMap<Int, ArityNode> = mutableMapOf())
        : ReteTree<Int>(children) {

        override val header = "Functor($functor)"

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause is Rule && functor == clause.head.functor -> {
                    val arity: Int = clause.head.arity
                    var child: ArityNode? = children[arity]

                    if (child === null) {
                        child = ArityNode(arity)
                        children[arity] = child
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
                when (clause) {
                    is Rule -> {
                        children[clause.head.arity]?.get(clause) ?: emptySequence()
                    }
                    else -> emptySequence()
                }

        override fun get(functor: String, arity: Int): Sequence<Clause> {
            return children[arity]?.get(functor, arity) ?: emptySequence()
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> =
                when {
                    limit == 0 -> {
                        emptySequence()
                    }
                    clause is Rule -> {
                        children[clause.head.arity]?.remove(clause, limit) ?: emptySequence()
                    }
                    else -> emptySequence()
                }

        override fun deepCopy(): FunctorNode = FunctorNode(functor, children.deepCopy({ it }, { it.deepCopy() }))
    }

    data class ArityNode(private val arity: Int, override val children: MutableMap<Term?, ReteTree<*>> = mutableMapOf())
        : ReteTree<Term?>(children) {

        init {
            require(arity >= 0) { "ArityNode arity should be greater than or equal to 0" }
        }

        override val header = "Arity($arity)"

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause !is Rule -> {
                    return
                }
                clause.head.arity > 0 -> {
                    val firstArg: Term = clause.head[0]
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

                    child.put(clause, before)
                }
                else -> {
                    var child: NoArgsNode? = children[null] as NoArgsNode?

                    if (child === null) {
                        child = NoArgsNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
                when {
                    clause !is Rule -> {
                        emptySequence()
                    }
                    clause.head.arity > 0 -> {
                        val firstArg: Term = clause.head[0]

                        children.entries.asSequence()
                                .filter { it.key !== null }
                                .filter { it.value is ArgNode }
                                .filter { it.key!!.matches(firstArg) }
                                .map { it.value }
                                .flatMap { it.get(clause) }
                    }
                    else -> {
                        children[null]?.get(clause) ?: emptySequence()
                    }
                }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> =
                when {
                    limit == 0 || clause !is Rule -> {
                        emptySequence()
                    }
                    clause.head.arity > 0 -> {
                        val firstArg: Term = clause.head[0]

                        val removed: MutableList<Clause> = mutableListOf()
                        for (child in children.entries.asSequence()
                                .filter { it.key !== null }
                                .filter { it.value is ArgNode }
                                .filter { it.key!!.matches(firstArg) }
                                .map { it.value }) {

                            removed += child.remove(clause, limit - removed.size)
                            if (removed.size == limit) break
                        }

                        removed.asSequence()
                    }
                    else -> {
                        children[null]?.remove(clause, limit) ?: emptySequence()
                    }
                }

        override fun deepCopy(): ArityNode = ArityNode(arity, children.deepCopy({ it }, { it.deepCopy() }))
    }

    data class NoArgsNode(override val children: MutableMap<Nothing?, RuleNode> = mutableMapOf())
        : ReteTree<Nothing?>(children) {

        override val header = "NoArguments"

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child: RuleNode? = children[null]

                    if (child == null) {
                        child = RuleNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
                when (clause) {
                    is Rule -> {
                        children[null]?.get(clause) ?: emptySequence()
                    }
                    else -> emptySequence()
                }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> =
                when {
                    limit == 0 || children.isEmpty() -> {
                        emptySequence()
                    }
                    clause is Rule -> {
                        children[null]?.remove(clause, limit) ?: emptySequence()
                    }
                    else -> emptySequence()
                }

        override fun deepCopy(): NoArgsNode = NoArgsNode(children.deepCopy({ it }, { it.deepCopy() }))
    }

    data class ArgNode(private val index: Int, private val term: Term, override val children: MutableMap<Term?, ReteTree<*>> = mutableMapOf())
        : ReteTree<Term?>(children) {

        init {
            require(index >= 0) { "ArgNode index should be greater than or equal to 0" }
        }

        override val header = "Argument($index, $term)"

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause !is Rule -> {
                    return
                }
                index < clause.head.arity - 1 -> { // if more arguments after "index" arg
                    val nextArg: Term = clause.head[index + 1]
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

                    child.put(clause, before)
                }
                else -> {
                    var child: RuleNode? = children[null] as RuleNode?

                    if (child === null) {
                        child = RuleNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause !is Rule -> {
                    emptySequence()
                }
                index < clause.head.arity - 1 -> {
                    val nextArg: Term = clause.head[index + 1]

                    children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }
                            .flatMap { it.get(clause) }
                }
                else -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
            }
        }

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return when {
                limit == 0 || clause !is Rule -> {
                    emptySequence()
                }
                index < clause.head.arity - 1 -> {
                    val nextArg: Term = clause.head[index + 1]

                    val removed: MutableList<Clause> = mutableListOf()
                    for (child in children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }) {

                        removed += child.remove(clause, limit - removed.size)
                        if (removed.size == limit) break
                    }

                    removed.asSequence()
                }
                else -> {
                    children[null]?.remove(clause, limit) ?: emptySequence()
                }
            }
        }

        override fun deepCopy(): ArgNode = ArgNode(index, term, children.deepCopy({ it }, { it.deepCopy() }))
    }

    data class RuleNode(private val rules: MutableList<Rule> = mutableListOf()) : ReteTree<Nothing>() {

        override val header = "Rules"

        override val clauses: Sequence<Clause>
            get() = rules.asSequence()

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> if (before) rules.add(0, clause) else rules.add(clause)
            }
        }

        override fun get(clause: Clause): Sequence<Clause> = clauses.filter { it matches clause }

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

        override fun deepCopy(): RuleNode = RuleNode(rules.toMutableList())

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

    abstract fun deepCopy(): ReteTree<K>

    internal abstract fun put(clause: Clause, before: Boolean = false)

    internal abstract fun remove(clause: Clause, limit: Int = 1): Sequence<Clause>

    internal fun removeAll(clause: Clause): Sequence<Clause> {
        return remove(clause, Int.MAX_VALUE)
    }

    abstract fun get(clause: Clause): Sequence<Clause>

    open fun get(functor: String, arity: Int): Sequence<Clause> = clauses

    open fun toString(treefy: Boolean): String {
        return if (treefy) {
            "$header {" +
                    children.values.joinToString(",\n\t", "\n\t", "\n") {
                        it.toString(treefy).replace("\n", "\n\t")
                    } +
                    "}"
        } else {
            toString()
        }
    }

    open val clauses: Sequence<Clause>
        get() = children.asSequence().flatMap { it.value.clauses }


    protected abstract val header: String

    companion object {

        private fun <K, V> MutableMap<K, V>.deepCopy(deepCopyKey: (K) -> K, deepCopyValue: (V) -> V): MutableMap<K, V> =
                entries.map { deepCopyKey(it.key) to deepCopyValue(it.value) }.toMap(mutableMapOf())

        fun of(clauses: Iterable<Clause>): ReteTree<*> =
                RootNode().apply { clauses.forEach { put(it) } }

        fun of(vararg clauses: Clause): ReteTree<*> = of(listOf(*clauses))
    }
}
