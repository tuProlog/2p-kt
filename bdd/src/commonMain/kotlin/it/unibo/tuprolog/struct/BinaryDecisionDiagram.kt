package it.unibo.tuprolog.struct

import kotlin.js.JsName

/**
 * A Binary Decision Diagram (BDD) is a rooted, directed, acyclic graph, which
 * consists of several decision nodes and terminal nodes and uses the concept of
 * Shannon Expansion to represent and encode complex Boolean Formulas.
 *
 * Each node of the diagram represents a single boolean entry with variable value
 * and part of a Boolean function. [T] is the type with which a variable is represented.
 * In the context of a formula, variables for which the [compareTo] method returns 0 indicate
 * the same Boolean variable.
 *
 * Each BDD node has a directed edge to two sub-BDDs: the "high"
 * BDD that leads to a true Terminal, and the "low" BDD that
 * leads to a false Terminal.
 *
 * @author Jason Dellaluce
 * */
sealed class BinaryDecisionDiagram<T : Comparable<T>> {

    @JsName("accept")
    abstract fun accept(visitor: BinaryDecisionDiagramVisitor<T>)

    /**
     * A [Terminal] is a BDD mode that has no edges to other BDDs, and represent
     * a non-variable known Boolean value (either True or False).
     * */
    data class Terminal<T : Comparable<T>>(val value: Boolean) : BinaryDecisionDiagram<T>() {
        override fun accept(visitor: BinaryDecisionDiagramVisitor<T>) {
            visitor.visit(this)
        }
    }

    /**
     * A [Var] is a BDD node representing a Boolean variable.
     * */
    data class Var<T : Comparable<T>>(
        val value: T,
        val low: BinaryDecisionDiagram<T> = Terminal(false),
        val high: BinaryDecisionDiagram<T> = Terminal(true)
    ) : BinaryDecisionDiagram<T>() {
        override fun accept(visitor: BinaryDecisionDiagramVisitor<T>) {
            visitor.visit(this)
        }
    }
}
