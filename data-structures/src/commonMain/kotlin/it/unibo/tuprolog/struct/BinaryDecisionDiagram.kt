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
 * A Terminal is a BDD mode that has no edges to other BDDs, and represents
 * a non-variable known Boolean value (either True or False).
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagram<T : Comparable<T>> {

    companion object {
    }

    @JsName("not")
    fun not(): BinaryDecisionDiagram<T>

    @JsName("and")
    infix fun and(other: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T>

    @JsName("or")
    infix fun or(other: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T>

    @JsName("accept")
    fun accept(visitor: BinaryDecisionDiagramVisitor<T>)
}
