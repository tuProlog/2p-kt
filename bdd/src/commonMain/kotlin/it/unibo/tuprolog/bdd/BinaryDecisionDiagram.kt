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
 * [compareTo] method returns 0 indicate the same Boolean variable.
 *
 * Each BDD node has a directed edge to two sub-BDDs: the "high"
 * BDD that leads to a true Terminal, and the "low" BDD that
 * leads to a false Terminal.
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
     * the inner structure of the diagram.
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

        /** [low] the a [BinaryDecisionDiagram] that leads to a 0-terminal
         * (a false terminal) */
        @JsName("low")
        val low: BinaryDecisionDiagram<T>

        /** [high] the a [BinaryDecisionDiagram] that leads to a 1-terminal
         * (a true terminal) */
        @JsName("high")
        val high: BinaryDecisionDiagram<T>

        override val isVariable: Boolean get() = true

        override val isTerminal: Boolean get() = false

        override fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E = visitor.visit(this)
    }

    companion object {
        /** Creates a new [BinaryDecisionDiagram] [Variable] from the given
         * value */
        @JsName("variableOf")
        fun <E : Comparable<E>> variableOf(value: E): BinaryDecisionDiagram<E> =
            variableOf(value, terminalOf(false), terminalOf(true))

        /** Creates a new [Variable] node from the given
         * value and low-high nodes. */
        @JsName("variableOfWithNodes")
        fun <E : Comparable<E>> variableOf(
            value: E,
            low: BinaryDecisionDiagram<E>,
            high: BinaryDecisionDiagram<E>,
        ): BinaryDecisionDiagram<E> = BinaryDecisionDiagramBuilder.defaultOf<E>().buildVariable(value, low, high)

        /** Creates a new [Terminal] node from
         * the given boolean value. */
        @JsName("terminalOf")
        fun <E : Comparable<E>> terminalOf(truth: Boolean): BinaryDecisionDiagram<E> =
            BinaryDecisionDiagramBuilder.defaultOf<E>().buildTerminal(truth)
    }
}
