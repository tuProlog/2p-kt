package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * Checks weather at least one node in a [BinaryDecisionDiagram] makes a certain predicate succeed.
 * @author Jason Dellaluce
 */
internal class AnyBinaryDecisionDiagramVisitor<T : Comparable<T>>(
    private val predicate: (T) -> Boolean,
) : BinaryDecisionDiagramVisitor<T> {
    var result = false

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
    }

    override fun visit(node: BinaryDecisionDiagram.Var<T>) {
        result = predicate(node.value)
        if (!result) {
            node.low.accept(this)
        }
        if (!result) {
            node.high.accept(this)
        }
    }
}
