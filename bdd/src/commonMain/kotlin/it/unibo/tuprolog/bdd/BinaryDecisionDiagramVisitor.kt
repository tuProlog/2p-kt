package it.unibo.tuprolog.bdd

import kotlin.js.JsName

/**
 * Implements the Visitor pattern over a [BinaryDecisionDiagram]. The two cases of [BinaryDecisionDiagram.Variable] and
 * [BinaryDecisionDiagram.Terminal] nodes must be handled differently. It is worth mentioning that this Visitor
 * abstraction must be the only (or at least preferred) way to explore the internal structure of a BDD.
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagramVisitor<T : Comparable<T>, E> {

    companion object

    @JsName("visitTerminal")
    fun visit(node: BinaryDecisionDiagram.Terminal<T>): E

    @JsName("visitVar")
    fun visit(node: BinaryDecisionDiagram.Variable<T>): E
}
