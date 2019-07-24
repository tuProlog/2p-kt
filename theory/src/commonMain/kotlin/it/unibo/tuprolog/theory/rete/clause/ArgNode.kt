package it.unibo.tuprolog.theory.rete.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.theory.rete.AbstractReteNode
import it.unibo.tuprolog.theory.rete.ReteNode
import it.unibo.tuprolog.theory.rete.RuleNode
import it.unibo.tuprolog.unify.Unification.Companion.matches

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
                    var child: ArgNode? = (children[nextArg] as ArgNode?)

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
                    }else{}

                    child.put(element, beforeOthers)
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
