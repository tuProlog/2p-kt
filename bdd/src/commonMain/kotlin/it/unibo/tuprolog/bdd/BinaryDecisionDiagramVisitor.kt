package it.unibo.tuprolog.bdd

import kotlin.js.JsName

/**
 * Implements the Visitor pattern over a [BinaryDecisionDiagram] to its
 * hierarchy, which only includes instances of [BinaryDecisionDiagram.Terminal]
 * and [BinaryDecisionDiagram.Variable]. This abstraction is the method of choice
 * to explore the internal structure of a BDD, dispatched through
 * [BinaryDecisionDiagram.accept] rather than by checking
 * [BinaryDecisionDiagram.isTerminal]/[BinaryDecisionDiagram.isVariable] and
 * casting manually. All BDD operators and utilities in this module
 * (e.g. [expansion], [apply], [any], [map]) are themselves implemented as
 * internal visitors.
 *
 * @author Jason Dellaluce
 */
interface BinaryDecisionDiagramVisitor<T : Comparable<T>, E> {
    companion object

    /**
     * Invoked by [BinaryDecisionDiagram.accept] when the visited node is a
     * [BinaryDecisionDiagram.Terminal].
     *
     * @param node the visited terminal node.
     * @return the visitor-specific result computed for [node].
     */
    @JsName("visitTerminal")
    fun visit(node: BinaryDecisionDiagram.Terminal<T>): E

    /**
     * Invoked by [BinaryDecisionDiagram.accept] when the visited node is a
     * [BinaryDecisionDiagram.Variable].
     *
     * @param node the visited variable node.
     * @return the visitor-specific result computed for [node].
     */
    @JsName("visitVariable")
    fun visit(node: BinaryDecisionDiagram.Variable<T>): E
}
