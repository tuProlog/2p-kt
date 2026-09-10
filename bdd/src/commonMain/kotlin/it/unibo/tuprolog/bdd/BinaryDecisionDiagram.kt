package it.unibo.tuprolog.bdd

import kotlin.js.JsName

/**
 * A Binary Decision Diagram (BDD) is a rooted, directed, acyclic graph, which
 * consists of several decision nodes and terminal nodes and uses the concept
 * of Shannon Expansion to represent and encode complex Boolean Formulas.
 *
 * Each node of the diagram represents a single boolean entry with variable
 * value and part of a Boolean function. [T] is the type with which a variable
 * is represented. In the context of a formula, variables for which the
 * `compareTo` method (from the [Comparable] bound on [T]) returns 0 indicate
 * the same Boolean variable.
 *
 * Each BDD node has a directed edge to two sub-BDDs: the "high"
 * BDD that leads to a true Terminal, and the "low" BDD that
 * leads to a false Terminal.
 *
 * In 2P-Kt, this data structure backs probabilistic logic programming
 * (`:solve-plp`, `:solve-problog`): a probabilistic query's *explanation* is
 * modeled as a Boolean formula over probabilistic clauses/facts (the BDD
 * variables), combined with [and], [or] and [not]. Since a BDD is a
 * canonical, compressed encoding of that formula, [expansion] can then be
 * used to compute the query's probability via Weighted Model Counting,
 * bottom-up over the diagram, without re-evaluating the original formula.
 * See `it.unibo.tuprolog.solve.problog.lib.knowledge.impl.BinaryDecisionDiagramExplanation`
 * for the concrete usage of this API to implement such an explanation.
 *
 * Basic usage:
 * ```kotlin
 * val burglary = bddOf(ComparablePair(0, "burglary", 0.7))
 * val earthquake = bddOf(ComparablePair(1, "earthquake", 0.2))
 * val solution = (bddOf(alarm) and burglary and earthquake) or (bddOf(otherAlarm) and burglary)
 * val probability = solution.expansion(0.0, 1.0) { node, low, high ->
 *     node.probabilityValue * high + (1.0 - node.probabilityValue) * low
 * }
 * ```
 *
 * @author Jason Dellaluce
 * */
interface BinaryDecisionDiagram<T : Comparable<T>> {
    /**
     * Returns true if this node is a [Variable] node.
     */
    @JsName("isVariable")
    val isVariable: Boolean

    /**
     * Returns true if this node is a [Terminal] node.
     */
    @JsName("isTerminal")
    val isTerminal: Boolean

    /**
     * Accepts an instance of [BinaryDecisionDiagramVisitor] as for the
     * visitor pattern. This is the method of preference for exploring
     * the inner structure of the diagram, since it distinguishes between
     * [Terminal] and [Variable] nodes without runtime type checks by the
     * caller.
     *
     * @param visitor the visitor whose `visit` overload matching this node's
     * actual type ([Terminal] or [Variable]) will be invoked.
     * @return the result of the invoked `visit` method.
     */
    @JsName("accept")
    fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E

    /**
     * A [Terminal] is a BDD mode that has no edges to other BDDs, and
     * represent a non-variable known Boolean value (either True or False)
     * */
    interface Terminal<T : Comparable<T>> : BinaryDecisionDiagram<T> {
        /** Boolean value of the terminal*/
        @JsName("truth")
        val truth: Boolean

        override val isVariable: Boolean get() = false

        override val isTerminal: Boolean get() = true

        override fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E = visitor.visit(this)
    }

    /**
     * A [Variable] is a BDD node representing a Boolean variable.
     * */
    interface Variable<T : Comparable<T>> : BinaryDecisionDiagram<T> {
        /** [value] represents the boolean variable */
        @JsName("value")
        val value: T

        /** [low] is the [BinaryDecisionDiagram] that leads to a 0-terminal
         * (a false terminal), i.e. the sub-diagram to follow when [value] is
         * assigned `false`. */
        @JsName("low")
        val low: BinaryDecisionDiagram<T>

        /** [high] is the [BinaryDecisionDiagram] that leads to a 1-terminal
         * (a true terminal), i.e. the sub-diagram to follow when [value] is
         * assigned `true`. */
        @JsName("high")
        val high: BinaryDecisionDiagram<T>

        override val isVariable: Boolean get() = true

        override val isTerminal: Boolean get() = false

        override fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E = visitor.visit(this)
    }

    companion object {
        /**
         * Creates a new [BinaryDecisionDiagram] [Variable] from the given
         * [value], whose [Variable.low] is a `false` [Terminal] and whose
         * [Variable.high] is a `true` [Terminal]. This represents the
         * simplest possible Boolean formula: a single variable, with no
         * further sub-structure. See also the [bddOf] shortcut function.
         *
         * @param value the value representing the Boolean variable.
         * @return a new single-variable [BinaryDecisionDiagram].
         */
        @JsName("variableOf")
        fun <E : Comparable<E>> variableOf(value: E): BinaryDecisionDiagram<E> =
            variableOf(value, terminalOf(false), terminalOf(true))

        /**
         * Creates a new [Variable] node from the given [value] and
         * `low`/`high` sub-diagrams, using the [BinaryDecisionDiagramBuilder.defaultOf]
         * builder (no reduction optimizations are applied).
         *
         * @param value the value representing the Boolean variable.
         * @param low the sub-diagram reached when [value] is `false`.
         * @param high the sub-diagram reached when [value] is `true`.
         * @return a new [Variable] node with the given [low]/[high] edges.
         */
        @JsName("variableOfWithNodes")
        fun <E : Comparable<E>> variableOf(
            value: E,
            low: BinaryDecisionDiagram<E>,
            high: BinaryDecisionDiagram<E>,
        ): BinaryDecisionDiagram<E> = BinaryDecisionDiagramBuilder.defaultOf<E>().buildVariable(value, low, high)

        /**
         * Creates a new [Terminal] node from the given boolean value, using
         * the [BinaryDecisionDiagramBuilder.defaultOf] builder. See also the
         * [bddTerminalOf] shortcut function.
         *
         * @param truth the boolean value (`true`/`false`) of the terminal.
         * @return a new [Terminal] node.
         */
        @JsName("terminalOf")
        fun <E : Comparable<E>> terminalOf(truth: Boolean): BinaryDecisionDiagram<E> =
            BinaryDecisionDiagramBuilder.defaultOf<E>().buildTerminal(truth)
    }
}
