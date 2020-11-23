package it.unibo.tuprolog.struct

import kotlin.js.JsName

/**
 * Implements the Visitor pattern over a [BinaryDecisionDiagram]. The two cases of Variable and
 * Terminal nodes must be handled differently. It is worth mentioning that this Visitor abstraction
 * must be the only (or at least preferred) way to explore the internal structure of a BDD.
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagramVisitor<T : Comparable<T>> {

    companion object

    @JsName("visitTerminal")
    fun visit(value: Boolean)

    @JsName("visitVar")
    fun visit(value: T, low: BinaryDecisionDiagram<T>, high: BinaryDecisionDiagram<T>)
}
