package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * Checks weather at least one node in a [BinaryDecisionDiagram] makes
 * a certain predicate succeed.
 *
 * @author Jason Dellaluce
 */
internal class AnyVisitor<T : Comparable<T>>(
    private val predicate: (T) -> Boolean,
) : BinaryDecisionDiagramVisitor<T, Boolean> {

    override fun visit(
        node: BinaryDecisionDiagram.Terminal<T>
    ): Boolean = false

    override fun visit(node: BinaryDecisionDiagram.Variable<T>): Boolean {
        var result = predicate(node.value)
        if (!result) {
            result = node.low.accept(this)
        }
        if (!result) {
            result = node.high.accept(this)
        }
        return result
    }
}
