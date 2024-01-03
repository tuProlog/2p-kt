package it.unibo.tuprolog.bdd

import kotlin.js.JsName

/**
 * Implements the Visitor pattern over a [BinaryDecisionDiagram] to its
 * hierarchy, which only includes instances of [BinaryDecisionDiagram.Terminal]
 * and [BinaryDecisionDiagram.Variable]. This abstraction is the method of choice
 * to explore the internal structure of a BDD.
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagramVisitor<T : Comparable<T>, E> {
    companion object

    @JsName("visitTerminal")
    fun visit(node: BinaryDecisionDiagram.Terminal<T>): E

    @JsName("visitVariable")
    fun visit(node: BinaryDecisionDiagram.Variable<T>): E
}
