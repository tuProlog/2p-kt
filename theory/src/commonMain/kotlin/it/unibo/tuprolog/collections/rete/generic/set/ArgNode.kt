package it.unibo.tuprolog.collections.rete.generic.set

import it.unibo.tuprolog.collections.rete.generic.AbstractIntermediateReteNode
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** The arg node indexes Clauses by argument, starting from first to all the others */
internal data class ArgNode(
    private val index: Int,
    private val term: Term,
    override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf(),
) : AbstractIntermediateReteNode<Term?, Rule>(children) {
    init {
        require(index >= 0) { "ArgNode index should be greater than or equal to 0" }
    }

    override val isArgNode: Boolean
        get() = true

    override fun asArgNode(): ArgNode = this

    override val header = "Argument($index, $term)"

    override fun put(
        element: Rule,
        beforeOthers: Boolean,
    ) = when {
        index < element.head.arity - 1 -> { // if more arguments after "index" arg
            val nextArg = element.head[index + 1]

            val child =
                children.getOrElse(nextArg) {
                    children
                        .retrieve(
                            keyFilter = { head -> head != null && head structurallyEquals nextArg },
                            typeChecker = { it.isArgNode },
                            caster = { it.castToArgNode() },
                        ).singleOrNull()
                }

            child ?: ArgNode(index + 1, nextArg)
                .also { children[nextArg] = it }
        }

        else -> children.getOrPut(null) { RuleNode() }
    }.put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> =
        when {
            index < element.head.arity - 1 -> {
                val nextArg = element.head[index + 1]
                children.retrieve(
                    keyFilter = { head -> head != null && head matches nextArg },
                    typeChecker = { it.isArgNode },
                    caster = { it.castToArgNode() },
                )
            }
            else -> sequenceOf(children[null])
        }

    override fun removeWithLimit(
        element: Rule,
        limit: Int,
    ): Sequence<Rule> =
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

    override fun deepCopy(): ArgNode =
        ArgNode(
            index,
            term,
            children.deepCopy({ it }, { it.deepCopy() }),
        )
}
